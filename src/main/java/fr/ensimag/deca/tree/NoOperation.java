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

/**
 * 
 * @author gl01
 * @date 01/01/2025
 */
public class NoOperation extends AbstractInst {

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        // nothing to do
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        // nothing to do
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(";");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    protected void codeGenByteInst(MethodVisitor mv, DecacCompiler compiler)  {
        // nothing to do
    }

   

}
