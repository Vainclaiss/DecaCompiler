package fr.ensimag.deca.tree;

import org.objectweb.asm.MethodVisitor;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BGE;
import fr.ensimag.ima.pseudocode.instructions.BLT;

/**
 *
 * @author gl01
 * @date 01/01/2025
 */
public class Lower extends AbstractOpIneq {

    public Lower(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e) {
        int indexR = Register.getIndexRegistreLibre();
        Register.setRegistreLibre(indexR, false);
        codeExp(compiler, indexR);
        Register.setRegistreLibre(indexR, true);
        if (branchIfTrue) {
            compiler.addInstruction(new BLT(e));
        } else {
            compiler.addInstruction(new BGE(e));
        }
    }

    @Override
    protected String getOperatorName() {
        return "<";
    }

    @Override
    protected void codeGenBytecode(MethodVisitor mv) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'codeGenBytecode'");
    }

    @Override
    protected void codeGenBool(MethodVisitor mv) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'codeGenBool'");
    }

}
