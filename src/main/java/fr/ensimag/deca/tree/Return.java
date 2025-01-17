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
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.RTS;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

public class Return extends AbstractInst {
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
            } else {
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
    protected void codeGenInst(DecacCompiler compiler, Label finLabel) {
        argument.codeExp(compiler, 2);
        compiler.addInstruction(new LOAD(Register.getR(compiler, 2), Register.R0));
        compiler.addInstruction(new BRA(finLabel));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.println(getOperatorName() + " " + argument.decompile() + ";");
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
    protected void codeGenInst(DecacCompiler compiler) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'codeGenInst'");
    }

}
