package fr.ensimag.deca.tree;
import fr.ensimag.deca.context.Type;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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
        codeExp(compiler, 2);
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

    @Override
protected void codeGenByteBool(MethodVisitor mv, boolean branchIfTrue, org.objectweb.asm.Label e) {
    getLeftOperand().codeByteExp(mv);

    getRightOperand().codeByteExp(mv);
    System.out.println("greater");

    Type leftType = getLeftOperand().getType();

    if (leftType.isInt()) {

        if (branchIfTrue) {
            mv.visitJumpInsn(Opcodes.IF_ICMPGT, e);
        } else {
            mv.visitJumpInsn(Opcodes.IF_ICMPLE, e);
        }
    } 
    else if (leftType.isFloat()) {
       
        mv.visitInsn(Opcodes.FCMPG);

        if (branchIfTrue) {
            mv.visitJumpInsn(Opcodes.IFGT, e);
        } else {
            mv.visitJumpInsn(Opcodes.IFLE, e);
        }
    }
    else {
        throw new UnsupportedOperationException("Greater: Unhandled type for comparison.");
    }
}

@Override
protected int getJumpOpcodeForInt() {
    return Opcodes.IF_ICMPGT;
}

@Override
protected int getJumpOpcodeForFloat() {
    return Opcodes.IFGT;
}






}
