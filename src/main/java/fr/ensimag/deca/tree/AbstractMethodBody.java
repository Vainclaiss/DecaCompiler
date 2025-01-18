package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;

import org.objectweb.asm.MethodVisitor;

public abstract class AbstractMethodBody extends Tree {

    public abstract void verifyMethodBody(DecacCompiler compiler, EnvironmentExp envExpParams,
            ClassDefinition currentClass, Type returnType) throws ContextualError;

    public abstract void codeGenMethodBody(DecacCompiler compiler, ClassDefinition currentClass, Label finLabel);
    public abstract void codeGenByteMethodBody(MethodVisitor mv,DecacCompiler compiler, Type returnType) throws ContextualError;

    public abstract void decompile(IndentPrintStream s);

}
