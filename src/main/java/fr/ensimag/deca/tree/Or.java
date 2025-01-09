package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Label;

/**
 *
 * @author gl01
 * @date 01/01/2025
 */
public class Or extends AbstractOpBool {

    public Or(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e) {
        And cond = new And(new Not(getLeftOperand()), new Not(getRightOperand()));
        cond.codeGenBool(compiler, !branchIfTrue, e);
    }

    @Override
    protected String getOperatorName() {
        return "||";
    }


}
