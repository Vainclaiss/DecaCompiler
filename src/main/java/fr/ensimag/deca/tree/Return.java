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
            throws ContextualError {

            if (returnType.isVoid()) {
                if (currentClass == null) {
                    throw new ContextualError("Error: Main does not return anything", getLocation());
                }
                else {
                    throw new ContextualError("Error: This method does not return anything", getLocation());
                }
            }
            setArgument(argument.verifyRValue(compiler, localEnv, currentClass, returnType));
        }

    protected void setArgument(AbstractExpr newArgument) {
        Validate.notNull(newArgument);
        this.argument = newArgument;
    }
    
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

}
