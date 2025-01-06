package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;

/**
 *
 * @author gl01
 * @date 01/01/2025
 */
public class NotEquals extends AbstractOpExactCmp {

    public NotEquals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e) {
        int indexR = Register.getIndexRegistreLibre();
        Register.setRegistreLibre(indexR, false);
        codeExp(compiler, indexR);
        Register.setRegistreLibre(indexR, true);
        if (branchIfTrue) {
            compiler.addInstruction(new BNE(e));
        }
        else {
            compiler.addInstruction(new BEQ(e));
        }
    }


    @Override
    protected String getOperatorName() {
        return "!=";
    }

}
