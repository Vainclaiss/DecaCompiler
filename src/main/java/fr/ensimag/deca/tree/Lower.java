package fr.ensimag.deca.tree;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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
protected void codeGenByteBool(MethodVisitor mv, boolean branchIfTrue, org.objectweb.asm.Label target) {
    getLeftOperand().codeGenByteInst(mv);

    getRightOperand().codeGenByteInst(mv);

    if (getType().isInt()) {
        if (branchIfTrue) {
            mv.visitJumpInsn(Opcodes.IF_ICMPLT, target);
        } else {
            mv.visitJumpInsn(Opcodes.IF_ICMPGE, target);
        }

    } else if (getType().isFloat()) {

        mv.visitInsn(Opcodes.FCMPG); 

        if (branchIfTrue) {
            mv.visitJumpInsn(Opcodes.IFLT, target);

        } else {

            mv.visitJumpInsn(Opcodes.IFGE, target);
        }

    } else {
        
        throw new UnsupportedOperationException(
            "Lower: unsupported type for '<': " + getType()
        );
    }
}


    @Override
    protected String getOperatorName() {
        return "<";
    }

    @Override
    protected int getJumpOpcodeForInt() {
        return Opcodes.IF_ICMPLT; 
    }

    @Override
    protected int getJumpOpcodeForFloat() {
        return Opcodes.IFLT;      
    }
    
    




}
