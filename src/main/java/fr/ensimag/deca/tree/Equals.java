package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.SEQ;

/**
 *
 * @author gl01
 * @date 01/01/2025
 */
public class Equals extends AbstractOpExactCmp {

    public Equals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e) {
        codeExp(compiler, 2);
        if (branchIfTrue) {
            compiler.addInstruction(new BEQ(e));
        }
        else {
            compiler.addInstruction(new BNE(e));
        }
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        super.codeExp(compiler, n);
        compiler.addInstruction(new SEQ(Register.getR(n)));
    }

    @Override
    protected String getOperatorName() {
        return "==";
    }    
    
}
