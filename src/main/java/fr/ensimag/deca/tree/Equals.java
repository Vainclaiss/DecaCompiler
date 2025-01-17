package fr.ensimag.deca.tree;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fr.ensimag.deca.context.Type;

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
        } else {
            compiler.addInstruction(new BNE(e));
        }
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        super.codeExp(compiler, n);
        compiler.addInstruction(new SEQ(Register.getR(compiler,n)));
    }
   

    @Override
    protected String getOperatorName() {
        return "==";
    }

    @Override
protected void codeGenByteBool(MethodVisitor mv, boolean branchIfTrue, org.objectweb.asm.Label e, DecacCompiler compiler) {
    getLeftOperand().codeByteExp(mv, compiler);

    getRightOperand().codeByteExp(mv, compiler);

    Type leftType = getLeftOperand().getType();
    if (leftType.isInt()) {
        if (branchIfTrue) {
            mv.visitJumpInsn(Opcodes.IF_ICMPEQ, e);
        } else {
            mv.visitJumpInsn(Opcodes.IF_ICMPNE, e);
        }
    } 
    else if (leftType.isFloat()) {
        mv.visitInsn(Opcodes.FCMPG);

        if (branchIfTrue) {
            mv.visitJumpInsn(Opcodes.IFEQ, e);
        } else {
            mv.visitJumpInsn(Opcodes.IFNE, e);
        }
    }
    else if (leftType.isBoolean()) {
        if (branchIfTrue) {
            mv.visitJumpInsn(Opcodes.IF_ICMPEQ, e);
        } else {
            mv.visitJumpInsn(Opcodes.IF_ICMPNE, e);
        }
    } 
    else {
        throw new UnsupportedOperationException("Equals codeGenByteBool: unhandled type for '==' operation");
    }
}

    
    @Override
    protected int getJumpOpcodeForInt() {
        return Opcodes.IF_ICMPEQ;
    }

    @Override
    protected int getJumpOpcodeForFloat() {
        return Opcodes.IFEQ;
    }

}
