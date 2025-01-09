package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Label;

/**
 *
 * @author gl01
 * @date 01/01/2025
 */
public class And extends AbstractOpBool {

    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e) {
        if (branchIfTrue) {
            Label eFin = new Label(e.toString() + "_fin");
            getLeftOperand().codeGenBool(compiler, false, eFin);
            getRightOperand().codeGenBool(compiler, true, e);
            compiler.addLabel(eFin);
        }
        else {
            getLeftOperand().codeGenBool(compiler, false, e);
            getRightOperand().codeGenBool(compiler, false, e);
        }
    }

    @Override
    protected String getOperatorName() {
        return "&&";
    }


}
