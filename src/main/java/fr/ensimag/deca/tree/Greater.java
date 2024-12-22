package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BGT;
import fr.ensimag.ima.pseudocode.instructions.BLE;
/**
 *
 * @author gl01
 * @date 01/01/2025
 */
public class Greater extends AbstractOpIneq {

    public Greater(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e) {
        int indexR = Register.getIndexRegistreLibre();
        Register.setRegistreLibre(indexR, false);
        codeExp(compiler, indexR);
        Register.setRegistreLibre(indexR, true);
        if (branchIfTrue) {
            compiler.addInstruction(new BGT(e));
        }
        else {
            compiler.addInstruction(new BLE(e));
        }
    }

    @Override
    protected String getOperatorName() {
        return ">";
    }

}
