// This is supporting software for CS321/CS322 Compilers and Language Design.
// Copyright (c) Portland State University
//---------------------------------------------------------------------------
// For CS322 W'16 (J. Li).
//  Scott Ewing
//

// IR1 code generator.
//
//
import java.util.*;
import java.io.*;
import ast.*;
import ir.*;

class IR1Gen {

  static class GenException extends Exception {
    public GenException(String msg) { super(msg); }
  }

  // For returning <src,code> pair from gen routines
  //
  static class CodePack {
    IR1.Src src;
    List<IR1.Inst> code;
    CodePack(IR1.Src src, List<IR1.Inst> code) { 
      this.src=src; this.code=code; 
    }
  }

  // The main routine
  //
  public static void main(String [] args) throws Exception {
    if (args.length == 1) {
      FileInputStream stream = new FileInputStream(args[0]);
      Ast1.Program p = new Ast1Parser(stream).Program();
      stream.close();
      IR1.Program ir = IR1Gen.gen(p);
      System.out.print(ir.toString());
    } else {
      System.out.println("You must provide an input file name.");
    }
  }

  // Ast1.Program ---
  // Ast1.Func[] funcs;
  //
  // AG:
  //   code: funcs.c  -- append all individual func.c
  //
  public static IR1.Program gen(Ast1.Program n) throws Exception {
    List<IR1.Func> code = new ArrayList<IR1.Func>();
    for (Ast1.Func f: n.funcs)
      code.add(gen(f));
    return new IR1.Program(code);

  }
  // FUNCTIONS
  static IR1.Func gen(Ast1.Func n) throws Exception {
    List<IR1.Inst> code = new ArrayList<IR1.Inst>();
    IR1.Global gname = new IR1.Global("_" + n.nm);

    List<IR1.Id> params = new ArrayList<IR1.Id>();
    for (Ast1.Param p: n.params)
        params.add(new IR1.Id(p.nm));
    
   List<IR1.Id> locals = new ArrayList<IR1.Id>();
   for (Ast1.VarDecl var: n.vars){
       locals.add(new IR1.Id(var.nm));
       code.addAll(gen(var));
    }

    for (Ast1.Stmt stmt: n.stmts)
        code.addAll(gen(stmt));

    if (n.t == null) {
        code.add(new IR1.Return());   
    }

    return new IR1.Func(gname, params, locals, code);  
  }

  // STATEMENTS

  static List<IR1.Inst> gen(Ast1.Stmt n) throws Exception {
    if (n instanceof Ast1.Block)  return gen((Ast1.Block) n);
    if (n instanceof Ast1.Assign) return gen((Ast1.Assign) n);
    if (n instanceof Ast1.If)     return gen((Ast1.If) n);
    if (n instanceof Ast1.While)  return gen((Ast1.While) n);
    if (n instanceof Ast1.Print)  return gen((Ast1.Print) n);
    if (n instanceof Ast1.Return) return gen((Ast1.Return) n);
    throw new GenException("Unknown Stmt: " + n);
  }

    // Ast1.Return ---
    // Ast.Exp val;
    static List<IR1.Inst> gen(Ast1.Return n) throws Exception{
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();
        if (n.val != null) {
            CodePack e = gen(n.val);
            code.addAll(e.code);
            code.add(new IR1.Return(e.src));
        }
        else
            code.add(new IR1.Return());

        return code;
    }


    // Ast1.Print ---
    // Ast1.Exp arg; // could be null
    static List<IR1.Inst> gen(Ast1.Print n) throws Exception{
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();

        CodePack e = gen(n.arg);
        code.addAll(e.code);
        List<IR1.Src> args = new ArrayList<IR1.Src>();
        args.add(e.src);

        String printType;
        if (e.src instanceof IR1.StrLit)
            printType = "_printStr";
        else
            printType = "_printInt";

        code.add(new IR1.Call(new IR1.Global(printType),args));

        return code;
    }

  // Ast1.VarDecl --- 
  //
  // Ast1.Type t
  // Ast1.String nm 
  // Ast1.Exp init // could be null
  // AG: 
  //    code: ???
  static List<IR1.Inst> gen(Ast1.VarDecl n) throws Exception {
    List<IR1.Inst> code = new ArrayList<IR1.Inst>();

    if (n.init != null){
        Ast1.Assign assign = new Ast1.Assign(new Ast1.Id(n.nm),n.init);
        code.addAll(gen(assign));
    }
    return code;
  }
 
  // Ast1.Block ---
  // Ast1.Stmt[] stmts;
  //
  // AG:
  //   code: {stmt.c}
  //
  static List<IR1.Inst> gen(Ast1.Block n) throws Exception {
    List<IR1.Inst> code = new ArrayList<IR1.Inst>();

    for (Ast1.Stmt s: n.stmts)
        code.addAll(gen(s));

    return code;
  }

  // Ast1.Assign ---
  // Ast1.Exp lhs;
  // Ast1.Exp rhs;
  //
  // AG:
  //   code: rhs.c + lhs.c + "lhs.s = rhs.v"
  //
  static List<IR1.Inst> gen(Ast1.Assign n) throws Exception {
      List<IR1.Inst> code = new ArrayList<IR1.Inst>();
      CodePack lhs,rhs;

      if (n.lhs instanceof Ast1.ArrayElm){
          rhs = gen(n.rhs);
          lhs = genAddr((Ast1.ArrayElm)n.lhs);
          
          code.addAll(rhs.code);
          code.addAll(lhs.code);

          code.add(new IR1.Store(new IR1.Addr(lhs.src),rhs.src));

      }
      else if (n.lhs instanceof Ast1.Id){
          lhs = gen(n.lhs);
          rhs = gen(n.rhs);

          code.addAll(rhs.code);
          code.addAll(lhs.code);

          code.add(new IR1.Move((IR1.Id)lhs.src,rhs.src));
      }
      else
        throw new GenException("Invalid LHS expression: " + n.lhs);

    return code;
  }

  // Ast1.If ---
  // Ast1.Exp cond;
  // Ast1.Stmt s1, s2;
  //
  // AG:
  //   newLabel: L1[,L2]
  //   code: cond.c 
  //         + "if cond.v == false goto L1" 
  //         + s1.c 
  //         [+ "goto L2"] 
  //         + "L1:" 
  //         [+ s2.c]
  //         [+ "L2:"]
  //
  static List<IR1.Inst> gen(Ast1.If n) throws Exception {
    List<IR1.Inst> code = new ArrayList<IR1.Inst>();


    //Shortcut: If condition is a boolean, we can evalute immediately
    CodePack cond = gen(n.cond);

    if (cond.src instanceof IR1.BoolLit)
    {
        boolean condition = ((IR1.BoolLit)cond.src).b;

        if (condition) // condition true means do statement 1
        {
            return gen(n.s1);
        }
        else // condition false means do statement 2
        {
            if (n.s2 != null)
                return gen(n.s2);

            return code;
        }
    }

    IR1.Label l1 = new IR1.Label();

    code.addAll(cond.code);

    IR1.CJump condJump = new IR1.CJump(IR1.ROP.EQ,cond.src,IR1.FALSE,l1); 
    code.add(condJump);

    code.addAll(gen(n.s1));

    IR1.Label l2 = null;
    if (n.s2 != null) 
    {
        l2 = new IR1.Label();
        code.add(new IR1.Jump(l2)); 
    }

    code.add(new IR1.LabelDec(l1));

    if (n.s2 != null)
    {
        if (l2 == null)
            throw new GenException("Unexpected error. Null label");
        code.addAll(gen(n.s2));
        code.add(new IR1.LabelDec(l2));
    }

    return code;
  }

  // Ast1.While ---
  // Ast1.Exp cond;
  // Ast1.Stmt s;
  //
  // AG:
  //   newLabel: L1,L2
  //   code: "L1:" 
  //         + cond.c 
  //         + "if cond.v == false goto L2" 
  //         + s.c 
  //         + "goto L1" 
  //         + "L2:"
  //
  static List<IR1.Inst> gen(Ast1.While n) throws Exception {
    List<IR1.Inst> code = new ArrayList<IR1.Inst>();

    IR1.Label l1 = new IR1.Label();

    code.add(new IR1.LabelDec(l1));
    CodePack cond = gen(n.cond);
    code.addAll(cond.code);

    IR1.Label l2 = new IR1.Label();
    code.add(new IR1.CJump(IR1.ROP.EQ, cond.src, IR1.FALSE, l2));
    code.addAll(gen(n.s));

    code.add(new IR1.Jump(l1));

    code.add(new IR1.LabelDec(l2));

    return code;
  }
  
  // EXPRESSIONS

  static CodePack gen(Ast1.Exp n) throws Exception {
    if (n instanceof Ast1.Binop)    return gen((Ast1.Binop) n);
    if (n instanceof Ast1.Unop)     return gen((Ast1.Unop) n);
    if (n instanceof Ast1.Id)       return gen((Ast1.Id) n);
    if (n instanceof Ast1.IntLit)   return gen((Ast1.IntLit) n);
    if (n instanceof Ast1.BoolLit)  return gen((Ast1.BoolLit) n);
    if (n instanceof Ast1.Call)     return gen((Ast1.Call) n);
    if (n instanceof Ast1.NewArray)  return gen((Ast1.NewArray) n);
    if (n instanceof Ast1.ArrayElm)  return gen((Ast1.ArrayElm) n);
    if (n instanceof Ast1.StrLit)    return gen((Ast1.StrLit) n);
    
    throw new GenException("Unknown Exp node: " + n);
  }

    static CodePack genAddr(Ast1.ArrayElm n) throws Exception{
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();
        CodePack e1 = gen(n.ar);
        code.addAll(e1.code);

        CodePack e2 = gen(n.idx);
        code.addAll(e2.code);

        IR1.Temp t1 = new IR1.Temp();
        code.add(new IR1.Binop(IR1.AOP.MUL, t1, e2.src, new IR1.IntLit(4)));
        IR1.Temp t2 = new IR1.Temp();
        code.add(new IR1.Binop(IR1.AOP.ADD, t2, e1.src, t1));

        return new CodePack(t2,code);
    }
    // Ast1.Call ---
    // Ast1.ID nm;
    // Ast1.Exp[] args;
    // AG:
    // newTemp: t
    // code: args.c + "t = call _nm(args.v)"

  static CodePack gen(Ast1.Call n) throws Exception{
      List<IR1.Inst> code = new ArrayList<IR1.Inst>();
      List<IR1.Src> srcs = new ArrayList<IR1.Src>();

      for (Ast1.Exp exp: n.args)
      {
          CodePack e = gen(exp);
          srcs.add(e.src);
          code.addAll(e.code);
      }
      IR1.Temp t = new IR1.Temp();
      code.add(new IR1.Call(new IR1.Global("_" + n.nm),srcs,t));

      return new CodePack(t,code);
  }

    // Ast1.NewArray --
    // Ast1.Type et;
    // Ast1.int len;
    // AG:
    // newTemp: t1, t2
    // code: "t1 = <IntLit>.i * 4
    //      + t2 = malloc (t1)"
    static CodePack gen(Ast1.NewArray n) throws Exception{
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();
        List<IR1.Src> args = new ArrayList<IR1.Src>();

        int offset = n.len * 4;

        IR1.Temp t1 = new IR1.Temp();
        args.add(new IR1.IntLit(offset));

        code.add(new IR1.Call(new IR1.Global("_malloc"),args,t1));

        return new CodePack(t1, code);
    }

    // Ast1.ArrayElm --
    // Ast1.Exp ar, idx; array object and index
    //
    // AG:
    //      newTemp t1, t2, t3
    //      Code: e1.c + e2.c + "t1 = e2.v * 4
    //                        + "t2 = e1.v + t1
    //                        + "t3 = [t2]"

    static CodePack gen(Ast1.ArrayElm n) throws Exception{
        List<IR1.Inst> code = new ArrayList<IR1.Inst>();
        CodePack e1 = gen(n.ar);
        code.addAll(e1.code);

        CodePack e2 = gen(n.idx);
        code.addAll(e2.code);

        IR1.Temp t1 = new IR1.Temp();
        code.add(new IR1.Binop(IR1.AOP.MUL, t1, e2.src, new IR1.IntLit(4)));
        IR1.Temp t2 = new IR1.Temp();
        code.add(new IR1.Binop(IR1.AOP.ADD, t2, e1.src, t1));
        IR1.Temp t3 = new IR1.Temp();
        code.add(new IR1.Load(t3,new IR1.Addr(t2)));

        return new CodePack(t3,code);
    }

  // Ast1.Binop ---
  // Ast1.BOP op;
  // Ast1.Exp e1,e2;
  //
  // AG:
  //   newTemp: t
  //   code: e1.c + e2.c
  //         + "t = e1.v op e2.v"
  //
  static CodePack gen(Ast1.Binop n) throws Exception {
    List<IR1.Inst> code = new ArrayList<IR1.Inst>();

    IR1.BOP bop = gen(n.op);

    IR1.Temp temp = null;

    if (bop == IR1.AOP.OR || bop == IR1.AOP.AND)
    {
        IR1.BoolLit operator;
        if (bop == IR1.AOP.OR)
            operator = new IR1.BoolLit(true);
        else
            operator = new IR1.BoolLit(false);
        
        CodePack e1 = gen(n.e1);
        CodePack e2 = gen(n.e2);
        // Relational operator shortcut for booleans.
        if (e1.src instanceof IR1.BoolLit && e2.src instanceof IR1.BoolLit)
        {
            boolean a = ((IR1.BoolLit)e1.src).b;
            boolean b = ((IR1.BoolLit)e2.src).b;
            Ast1.BoolLit result;
            if (operator.b) // operator == true means OR
                result = new Ast1.BoolLit(a || b);
            else
                result = new Ast1.BoolLit(a && b);
            
            return gen(result); 
        }

        IR1.Label l = new IR1.Label();
        temp = new IR1.Temp();

        code.add(new IR1.Move(temp, operator));
        code.addAll(e1.code);

        code.add(new IR1.CJump(IR1.ROP.EQ, e1.src, operator, l));

        code.addAll(e2.code);

        code.add(new IR1.Move(temp, e2.src));
        code.add(new IR1.LabelDec(l));
    }
    else
    { 
        CodePack e1 = gen(n.e1);
        CodePack e2 = gen(n.e2);
 
        //if e1.src and e2.src are integers, just add them and return it.
        if (e1.src instanceof IR1.IntLit && e2.src instanceof IR1.IntLit)
        {   if (bop instanceof IR1.AOP)
                return gen(helper((IR1.AOP)bop,(IR1.IntLit) e1.src, (IR1.IntLit)e2.src));
            else 
                return gen(helper((IR1.ROP)bop, (IR1.IntLit) e1.src, (IR1.IntLit)e2.src));
        }
        else if (e1.src instanceof IR1.BoolLit && e2.src instanceof IR1.BoolLit)
        {
            boolean a = ((IR1.BoolLit)e1.src).b;
            boolean b = ((IR1.BoolLit)e2.src).b;

            if (bop == IR1.ROP.EQ) return gen(new Ast1.BoolLit(a == b));
            if (bop == IR1.ROP.NE) return gen(new Ast1.BoolLit(a != b));
        }
        else 
        {
            temp = new IR1.Temp();
            code.addAll(e1.code);
            code.addAll(e2.code);
            IR1.Binop result = new IR1.Binop(bop, temp, e1.src, e2.src);
            code.add(result);
        }
    }    
   // if (temp == null)
   //     throw new GenException("Error, null temp value.");

    return new CodePack(temp,code);
  }

  // Ast1.Unop ---
  // Ast1.UOP op;
  // Ast1.Exp e;
  //
  // AG:
  //   newTemp: t
  //   code: e.c + "t = op e.v"
  //
  static CodePack gen(Ast1.Unop n) throws Exception {
    List<IR1.Inst> code = new ArrayList<IR1.Inst>();

    CodePack e = gen(n.e);
    String uop = n.op.toString();
    
    if (e.src instanceof IR1.BoolLit && uop == "!")
    {  
        return gen(new Ast1.BoolLit(!((IR1.BoolLit)e.src).b));
    }

    IR1.Temp temp = new IR1.Temp();
    code.addAll(e.code);

    if (uop == "!")
        code.add(new IR1.Unop(IR1.UOP.NOT,temp,e.src));
    else if (uop == "-")
        code.add(new IR1.Unop(IR1.UOP.NEG,temp,e.src));
    else 
       throw new GenException("Unknown Unop: " + n.op);

    return new CodePack(temp,code);
  }
  
  // Ast1.Id ---
  // String nm;
  //
  static CodePack gen(Ast1.Id n) throws Exception {
      IR1.Id id = new IR1.Id(n.nm);
      return new CodePack(id,new ArrayList<IR1.Inst>());
  }

    // Ast1.StrLit --
    // String s;
    static CodePack gen(Ast1.StrLit n) throws Exception {
        IR1.StrLit str = new IR1.StrLit(n.s);
        return new CodePack(str,new ArrayList<IR1.Inst>());
    }

  // Ast1.IntLit ---
  // int i;
  //
  static CodePack gen(Ast1.IntLit n) throws Exception {

    IR1.IntLit intlit = new IR1.IntLit(n.i);

    return new CodePack(intlit,new ArrayList<IR1.Inst>());
  }

  // Ast1.BoolLit ---
  // boolean b;
  //
  static CodePack gen(Ast1.BoolLit n) throws Exception {

    IR1.BoolLit boollit = new IR1.BoolLit(n.b);
    return new CodePack(boollit,new ArrayList<IR1.Inst>());
  }

  // OPERATORS

  static IR1.BOP gen(Ast1.BOP op) {
    IR1.BOP irOp = null;
    switch (op) {
    case ADD: irOp = IR1.AOP.ADD; break;
    case SUB: irOp = IR1.AOP.SUB; break;
    case MUL: irOp = IR1.AOP.MUL; break;
    case DIV: irOp = IR1.AOP.DIV; break;
    case AND: irOp = IR1.AOP.AND; break;
    case OR:  irOp = IR1.AOP.OR;  break;
    case EQ:  irOp = IR1.ROP.EQ;  break;
    case NE:  irOp = IR1.ROP.NE;  break;
    case LT:  irOp = IR1.ROP.LT;  break;
    case LE:  irOp = IR1.ROP.LE;  break;
    case GT:  irOp = IR1.ROP.GT;  break;
    case GE:  irOp = IR1.ROP.GE;  break;
    }
    return irOp;
  }
    
    static Ast1.IntLit helper(IR1.AOP op, IR1.IntLit x, IR1.IntLit y) throws Exception{
        Ast1.IntLit result;
        int value = 0;
        switch ((IR1.AOP) op) {
            case ADD: value = x.i + y.i; break;
            case SUB: value = x.i - y.i; break;
            case MUL: value = x.i * y.i; break;
            case DIV: value = x.i / y.i; break;

        }
       
        return new Ast1.IntLit(value);
    }

    static Ast1.BoolLit helper(IR1.ROP op, IR1.IntLit x, IR1.IntLit y) throws Exception 
    {
        boolean value = false;
        switch(op){
            case EQ: value = x.i == y.i; break;
            case NE: value = x.i != y.i; break;
            case LT: value = x.i < y.i ; break;
            case LE: value = x.i <= y.i; break;
            case GT: value = x.i > y.i ; break;
            case GE: value = x.i >= y.i; break;
        }
        return new Ast1.BoolLit(value);
    }
}
