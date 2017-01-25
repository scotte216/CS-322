// This is supporting software for CS322 Compilers and Language Design II
// Copyright (c) Portland State University
//---------------------------------------------------------------------------
// For CS322 W'16 (J. Li).
// Scott Ewing

// IR1 interpreter. (A starter version)
//
//

import java.util.*;
import java.io.*;

import ir.*;

public class IR1Interp {

    static class IntException extends Exception {
        public IntException(String msg) {
            super(msg);
        }
    }

    //-----------------------------------------------------------------
    // Value Representation
    //-----------------------------------------------------------------
    //
    abstract static class Val {
    }

    // -- Integer values
    //
    static class IntVal extends Val {
        int i;

        IntVal(int i) {
            this.i = i;
        }

        public String toString() {
            return "" + i;
        }
    }

    // -- Boolean values
    //
    static class BoolVal extends Val {
        boolean b;

        BoolVal(boolean b) {
            this.b = b;
        }

        public String toString() {
            return "" + b;
        }
    }

    // -- String values
    //
    static class StrVal extends Val {
        String s;

        StrVal(String s) {
            this.s = s;
        }

        public String toString() {
            return s;
        }
    }

    // -- A special "undefined" value
    //
    static class UndVal extends Val {
        public String toString() {
            return "UndVal";
        }
    }

    //-----------------------------------------------------------------
    // Storage Organization
    //-----------------------------------------------------------------
    //

    // -- Global heap memory
    //
    static ArrayList<Val> memory;

    // -- Environment for tracking var, temp, and param's values
    //    (one copy per fuction invocation)
    //
    static class Env extends HashMap<String, Val> {
    }

    //-----------------------------------------------------------------
    // Other Data Structures
    //-----------------------------------------------------------------
    //
    // GUIDE:
    //  You have control over these. Either define look-up tables for
    //  functions and labels, or searching functions.
    //
    //mapping a function name to the function's definition
    static HashMap<String, IR1.Func> funcMap;
    static class LabMap extends HashMap<String, Integer> {}
    static HashMap<String, LabMap> labelMap;
    // -- Useful global variables
    //
    static final int CONTINUE = -1;    // execution status
    static final int RETURN = -2;        // execution status
    static Val retVal = null;             // for return value passing


    //-----------------------------------------------------------------
    // The Main Method
    //-----------------------------------------------------------------
    //
    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            FileInputStream stream = new FileInputStream(args[0]);
            IR1.Program p = new IR1Parser(stream).Program(); // originally was 'new ir1Parser...'
            stream.close();
            IR1Interp.execute(p);
        } else {
            System.out.println("You must provide an input file name.");
        }
    }

    //-----------------------------------------------------------------
    // Top-Level Nodes
    //-----------------------------------------------------------------
    //

    // Program ---
    //  Func[] funcs;
    //
    // GUIDE:
    // 1. Establish the look-up tables (if you plan to use them).
    // 2. Look up or search for function '_main'.
    // 3. Start interpreting from '_main' with an empty Env.
    //
    public static void execute(IR1.Program n) throws Exception {

        funcMap = new HashMap<>();
        labelMap = new HashMap<>();
        for (IR1.Func f: n.funcs) {
            funcMap.put(f.gname.s, f);
            LabMap labMap = new LabMap();
            int index = 0;

            for (IR1.Inst i: f.code)
            {
                if (i instanceof IR1.LabelDec)
                {
                    IR1.Label label = ((IR1.LabelDec)i).lab;

                    labMap.put(label.name, index);
                }
                index++;
            }
            labelMap.put(f.gname.s, labMap);

        }

        IR1.Func main = funcMap.get("_main");
        Env env = new Env();
        env.put("self", new StrVal("_main"));
        execute(main, env);
    }

    // Func ---
    //  Global gname;
    //  Id[] params;
    //  Id[] locals;
    //  Inst[] code;
    //
    // GUIDE:
    //  - Implement the fetch-execute loop.
    //  - The parameter 'env' is the function's initial Env, which
    //    contains its parameters' values.
    //
    static void execute(IR1.Func n, Env env) throws Exception {
        int idx = 0;
        while (idx < n.code.length) {
            int next = execute(n.code[idx], env);
            if (next == CONTINUE)
                idx++;
            else if (next == RETURN)
                break;
            else
                idx = next;
        }
    }

    // Dispatch execution to an individual Inst node.
    //
    static int execute(IR1.Inst n, Env env) throws Exception {
        if (n instanceof IR1.Binop) return execute((IR1.Binop) n, env);
        if (n instanceof IR1.Unop) return execute((IR1.Unop) n, env);
        if (n instanceof IR1.Move) return execute((IR1.Move) n, env);
        if (n instanceof IR1.Load) return execute((IR1.Load) n, env);
        if (n instanceof IR1.Store) return execute((IR1.Store) n, env);
        if (n instanceof IR1.Call) return execute((IR1.Call) n, env);
        if (n instanceof IR1.Return) return execute((IR1.Return) n, env);
        if (n instanceof IR1.Jump) return execute((IR1.Jump) n, env);
        if (n instanceof IR1.CJump) return execute((IR1.CJump) n, env);
        if (n instanceof IR1.LabelDec) return CONTINUE;
        throw new IntException("Unknown Inst: " + n);
    }

    //-----------------------------------------------------------------
    // Individual Instruction Nodes
    //-----------------------------------------------------------------
    //
    // - Each execute() routine returns CONTINUE, RETURN, or a new idx
    //   (target of jump).
    //

    // Binop ---
    //  BOP op;
    //  Dest dst;
    //  Src src1, src2;
    //
    // GUIDE:
    // 1. Evaluate the operands, then perform the operation.
    // 2. Update 'dst's entry in the Env with operation's result.
    //
    static int execute(IR1.Binop n, Env env) throws Exception {
        Val src1 = evaluate(n.src1, env);
        Val src2 = evaluate(n.src2, env);
        Val result = null;

        if (src1 instanceof IntVal && src2 instanceof IntVal)
        {
            int src1Int = ((IntVal) src1).i;
            int src2Int = ((IntVal) src2).i;

            if (n.op instanceof IR1.AOP)
            {
                IR1.AOP aop = (IR1.AOP)n.op;
                if (aop == IR1.AOP.ADD) result = new IntVal(src1Int + src2Int);
                if (aop == IR1.AOP.SUB) result = new IntVal(src1Int - src2Int);
                if (aop == IR1.AOP.MUL) result = new IntVal(src1Int * src2Int);
                if (aop == IR1.AOP.DIV) result = new IntVal(src1Int / src2Int);
            }

            if (n.op instanceof IR1.ROP)
            {
                IR1.ROP rop = (IR1.ROP)n.op;
                if (rop == IR1.ROP.EQ) result = new BoolVal(src1Int == src2Int);
                if (rop == IR1.ROP.NE) result = new BoolVal(src1Int != src2Int);
                if (rop == IR1.ROP.LT) result = new BoolVal(src1Int <  src2Int);
                if (rop == IR1.ROP.LE) result = new BoolVal(src1Int <= src2Int);
                if (rop == IR1.ROP.GT) result = new BoolVal(src1Int >  src2Int);
                if (rop == IR1.ROP.GE) result = new BoolVal(src1Int >= src2Int);
            }
        }
        else if (src1 instanceof BoolVal && src2 instanceof BoolVal)
        {
            boolean src1Bool = ((BoolVal) src1).b;
            boolean src2Bool = ((BoolVal) src2).b;

            if (n.op instanceof IR1.AOP)
            {
                IR1.AOP aop = (IR1.AOP)n.op;
                if (aop == IR1.AOP.AND) result = new BoolVal(src1Bool && src2Bool);
                if (aop == IR1.AOP.OR) result = new BoolVal(src1Bool || src2Bool);
            }

            if (n.op instanceof IR1.ROP)
            {
                IR1.ROP rop = (IR1.ROP)n.op;
                if (rop == IR1.ROP.EQ) result = new BoolVal(src1Bool == src1Bool);
                if (rop == IR1.ROP.NE) result = new BoolVal(src1Bool != src2Bool);
            }
        }
        else
            throw new IntException("Operands are both ints or both bools");

        env.put(n.dst.toString(), result);
        return CONTINUE;
    }

    // Unop ---
    //  UOP op;
    //  Dest dst;
    //  Src src;
    //
    // GUIDE:
    // 1. Evaluate the operand, then perform the operation.
    // 2. Update 'dst's entry in the Env with operation's result.
    //
    static int execute(IR1.Unop n, Env env) throws Exception {
        Val src = evaluate(n.src, env);
        Val result = null;

        if (n.op == IR1.UOP.NEG)
        {
            result = new IntVal(-((IntVal)src).i);
        }
        if (n.op == IR1.UOP.NOT)
        {
            result = new BoolVal(!((BoolVal)src).b);
        }

        env.put(n.dst.toString(), result);
        return CONTINUE;
    }

    // Move ---
    //  Dest dst;
    //  Src src;
    //
    // GUIDE:
    //  Evaluate 'src', then update 'dst's entry in the Env.
    //
    static int execute(IR1.Move n, Env env) throws Exception {
        Val src = evaluate(n.src, env);

        env.put(n.dst.toString(), src);

        return CONTINUE;
    }

    // Load ---
    //  Dest dst;
    //  Addr addr;
    //
    // GUIDE:
    //  Evaluate 'addr' to a memory index, then retrieve the stored
    //  value from memory and update 'dst's entry in the Env.
    //
    static int execute(IR1.Load n, Env env) throws Exception {
        int addr = evaluate(n.addr, env);

        Val value = memory.get(addr);
        env.put(n.dst.toString(), value);

        return CONTINUE;
    }

    // Store ---
    //  Addr addr;
    //  Src src;
    //
    // GUIDE:
    // 1. Evaluate 'src' to a value.
    // 2. Evaluate 'addr' to a memory index, then store the value
    //    to the memory entry.
    //
    static int execute(IR1.Store n, Env env) throws Exception {
        Val src = evaluate(n.src, env);
        int index = evaluate(n.addr, env);
        memory.set(index,src);

        return CONTINUE;
    }

    // CJump ---
    //  ROP op;
    //  Src src1, src2;
    //  Label lab;
    //
    // GUIDE:
    // 1. Evaluate the cond op.
    // 2. If cond is true, find and return the instruction index
    //    of the jump target label; otherwise return CONTINUE.
    //
    static int execute(IR1.CJump n, Env env) throws Exception {
        Val src1 = evaluate(n.src1, env);
        Val src2 = evaluate(n.src2, env);
        boolean result = false;

        if (src1 instanceof BoolVal && src2 instanceof BoolVal)
        {
            boolean src1Bool = ((BoolVal)src1).b;
            boolean src2Bool = ((BoolVal)src2).b;
            if (n.op == IR1.ROP.EQ) result = src1Bool == src2Bool;
            if (n.op == IR1.ROP.NE) result = src1Bool != src2Bool;
        }
        else if (src1 instanceof IntVal && src2 instanceof IntVal)
        {
            int src1Int = ((IntVal)src1).i;
            int src2Int = ((IntVal)src2).i;
            if (n.op == IR1.ROP.EQ) result = src1Int == src2Int;
            if (n.op == IR1.ROP.NE) result = src1Int != src2Int;
            if (n.op == IR1.ROP.LT) result = src1Int <  src2Int;
            if (n.op == IR1.ROP.LE) result = src1Int <= src2Int;
            if (n.op == IR1.ROP.GT) result = src1Int >  src2Int;
            if (n.op == IR1.ROP.GE) result = src1Int >= src2Int;
        }
        else
            throw new IntException("mismatched operands in conditional jump?");

        if (result)
        {
            String funcName = ((StrVal)env.get("self")).s;
            LabMap map = labelMap.get(funcName);
            return map.get(n.lab.toString());
        }
        else
            return CONTINUE;
    }

    // Jump ---
    //  Label lab;
    //
    // GUIDE:
    //  Find and return the instruction index of the jump target label.
    //
    static int execute(IR1.Jump n, Env env) throws Exception {
        String funcName = ((StrVal)env.get("self")).s;
        LabMap map = labelMap.get(funcName);

        return map.get(n.lab.toString());
    }

    // Call ---
    //  Global gname;
    //  Src[] args;
    //  Dest rdst;
    //
    // GUIDE:
    // 1. Evaluate the arguments to values.
    // 2. Create a new Env for the callee; pair function's parameter
    //    names with arguments' values, and add them to the new Env.
    // 3. Find callee's Func node and switch to execute it.
    // 4. If 'rdst' is not null, update its entry in the Env with
    //    the return value (should be avaiable in variable 'retVal').
    //
    static int execute(IR1.Call n, Env env) throws Exception {

        if (n.gname.s.equals("_printInt")){
            String out = null;
            Val arg = evaluate(n.args[0],env);

            if (arg != null)
            {
                if (arg instanceof IntVal)
                    out = arg.toString();
                else if (arg instanceof BoolVal)
                {
                    boolean b = ((BoolVal)arg).b;
                    if (b)
                        out = "true";
                    else
                        out = "false";
                }
            }
            System.out.println(out);
        }
        else if (n.gname.s.equals("_printStr")){
            String out = null;

            if (n.args != null && n.args.length > 0 && n.args[0] != null)
                out = ((IR1.StrLit)n.args[0]).s;
            if (out == null)
                System.out.println();
            else
                System.out.println(out);
        }
        else if (n.gname.s.equals("_malloc")) {
            //n.arg[0] is the size
            if (memory != null)
                retVal = new IntVal(memory.size());
            else
            {
                memory = new ArrayList<>();
                retVal = new IntVal(memory.size());
            }
            Val sizeVal = evaluate(n.args[0], env);

            //int size = ((IR1.IntLit) n.args[0]).i;
            int size = ((IntVal)sizeVal).i;
            for (int i = 0; i < size; ++i)
                memory.add(new UndVal());

        }
        else
        {
            Env funcEnv = new Env();
            IR1.Func func = funcMap.get(n.gname.s);

            funcEnv.put("self", new StrVal(n.gname.s));
            IR1.Id[] params = func.params;

            for (int i = 0; i < params.length; ++i)
                funcEnv.put(params[i].s,evaluate(n.args[i],env));

            execute(func, funcEnv);
        }

        if (n.rdst != null)
            env.put(n.rdst.toString(), retVal);
        return CONTINUE;
    }

    // Return ---
    //  Src val;
    //
    // GUIDE:
    //  If 'val' is not null, set it to the variable 'retVal'.
    //
    static int execute(IR1.Return n, Env env) throws Exception {

        if (n.val != null)
            retVal = evaluate(n.val, env);

        return RETURN;
    }

    //-----------------------------------------------------------------
    // Address and Operand Nodes.
    //-----------------------------------------------------------------
    //
    // - Each has an evaluate() routine.
    //

    // Address ---
    //  Src base;
    //  int offset;
    //
    // GUIDE:
    // 1. Evaluate 'base' to an integer, then add 'offset' to it.
    // 2. Return the result (which should be an index to memory).
    //
    static int evaluate(IR1.Addr n, Env env) throws Exception {
        int loc = ((IntVal)evaluate(n.base, env)).i;
        return loc + n.offset;
    }

    // Src Nodes
    //  -> Temp | Id | IntLit | BooLit | StrLit
    //
    // GUIDE:
    //  In each case, the evaluate() routine returns a Val object.
    //  - For Temp and Id, look up their value from the Env, wrap
    //    it in a Val and return.
    //  - For the literals, wrap their value in a Val and return.
    //
    static Val evaluate(IR1.Src n, Env env) throws Exception {
        Val val = null;
        if (n instanceof IR1.Temp)    val = env.get(n.toString());
        if (n instanceof IR1.Id)      val = env.get(n.toString());
        if (n instanceof IR1.IntLit)  val = new IntVal(((IR1.IntLit)n).i);
        if (n instanceof IR1.BoolLit) val = new BoolVal(((IR1.BoolLit)n).b);
        if (n instanceof IR1.StrLit)  val = new StrVal(((IR1.StrLit)n).s);
        return val;
    }

    // Dst Nodes
    //  -> Temp | Id
    //
    // GUIDE:
    //  For both cases, look up their value from the Env, wrap it
    //  in a Val and return.
    //
    static Val evaluate(IR1.Dest n, Env env) throws Exception {
        Val val = null;

        if (n instanceof IR1.Temp) val = env.get(n.toString());
        if (n instanceof IR1.Id)   val = env.get(n.toString());

        return val;
    }

}
