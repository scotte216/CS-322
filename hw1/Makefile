# Makefile for CS322 Lab3.
#
JFLAGS = -g
JC = javac
JCC = javacc

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

irgen: 	ast/Ast1.class ast/Ast1Parser.class ir/IR1.class IR1Gen.class

clean:
	'rm' ast/*.class ir/*.class *.class


