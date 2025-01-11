package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.objectweb.asm.MethodVisitor;

public class Return extends AbstractInst{
    private AbstractExpr argument;

    public Return(AbstractExpr argument) {
        this.argument = argument;
    }

    protected String getOperatorName() {
        return "return";
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
                              ClassDefinition currentClass, Type returnType)
            throws ContextualError {}

    public AbstractExpr getArgument() {
        return argument;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
            getArgument().codeGenPrint(compiler);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.println("return " + argument.decompile() + ";");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        argument.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        argument.prettyPrint(s, prefix, true);
    }

    @Override
    protected void codeGenByteInst(MethodVisitor mv) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'codeGenByteInst'");
    }

}
