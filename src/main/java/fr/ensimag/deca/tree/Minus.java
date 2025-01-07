package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.execerrors.OverflowError;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.SUB;

/**
 * @author gl01
 * @date 01/01/2025
 */
public class Minus extends AbstractOpArith {
    public Minus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler, DVal op1, GPRegister r) {
        compiler.addInstruction(new SUB(op1, r));
        if (getType().isFloat() && !compiler.getCompilerOptions().getSkipExecErrors()) {
            compiler.addExecError(OverflowError.INSTANCE);
            compiler.addInstruction(new BOV(OverflowError.INSTANCE.getLabel()));
        }
    }

    @Override
    protected String getOperatorName() {
        return "-";
    }
    
}
