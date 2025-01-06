package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.ADD;


/**
 * @author gl01
 * @date 01/01/2025
 */
public class Plus extends AbstractOpArith {
    public Plus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
 

    @Override
    protected void codeGenInst(DecacCompiler compiler, DVal op1, GPRegister r) {
        compiler.addInstruction(new ADD(op1, r));
    }

    @Override
    protected String getOperatorName() {
        return "+";
    }
}
