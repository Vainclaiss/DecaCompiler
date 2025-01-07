package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.execerrors.ZeroDivisionError;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.DIV;
import fr.ensimag.ima.pseudocode.instructions.QUO;

/**
 *
 * @author gl01
 * @date 01/01/2025
 */
public class Divide extends AbstractOpArith {
    public Divide(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler, DVal op1, GPRegister r) {
        Type t1 = getLeftOperand().getType();
        Type t2 = getRightOperand().getType();

        if (t1.isFloat() && t2.isFloat()) {
            compiler.addInstruction(new DIV(op1, r));
        } else if (t1.isInt() && t2.isInt()) {
            compiler.addInstruction(new QUO(op1, r));
            if (!compiler.getCompilerOptions().getSkipExecErrors()) {
                compiler.addExecError(ZeroDivisionError.INSTANCE);
                compiler.addInstruction(new BOV(ZeroDivisionError.INSTANCE.getLabel()));
            }
        }
    }

    @Override
    protected String getOperatorName() {
        return "/";
    }

}
