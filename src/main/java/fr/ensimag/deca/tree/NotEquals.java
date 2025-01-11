package fr.ensimag.deca.tree;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.DecacInternalError;
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
        codeExp(compiler, 2);
        if (branchIfTrue) {
            compiler.addInstruction(new BNE(e));
        }
        else {
            compiler.addInstruction(new BEQ(e));
        }
    }

    
  @Override
    protected void codeGenByteInst(MethodVisitor mv) {
        getLeftOperand().codeGenByteInst(mv);

        getRightOperand().codeGenByteInst(mv);

        if (getType().isInt() || getType().isBoolean()) {
          
            org.objectweb.asm.Label labelTrue = new org.objectweb.asm.Label();
            org.objectweb.asm.Label labelEnd = new org.objectweb.asm.Label();

            mv.visitJumpInsn(Opcodes.IF_ICMPNE, labelTrue);

            mv.visitInsn(Opcodes.ICONST_0);

            mv.visitJumpInsn(Opcodes.GOTO, labelEnd);

            mv.visitLabel(labelTrue);
            mv.visitInsn(Opcodes.ICONST_1);

            mv.visitLabel(labelEnd);

        } else if (getType().isFloat()) {

            org.objectweb.asm.Label labelTrue = new org.objectweb.asm.Label();
            org.objectweb.asm.Label labelEnd = new org.objectweb.asm.Label();
            
            mv.visitInsn(Opcodes.FCMPG);          
            mv.visitJumpInsn(Opcodes.IFNE, labelTrue); 

            mv.visitInsn(Opcodes.ICONST_0);
            mv.visitJumpInsn(Opcodes.GOTO, labelEnd);

            mv.visitLabel(labelTrue);
            mv.visitInsn(Opcodes.ICONST_1);

            mv.visitLabel(labelEnd);

        } else {
            throw new DecacInternalError("NotEquals: Unsupported type for '!=' operator: " + getType());
        }
    }

    @Override
    protected String getOperatorName() {
        return "!=";
    }

    @Override
    protected int getJumpOpcodeForInt() {
        return Opcodes.IF_ICMPNE;
    }

    @Override
    protected int getJumpOpcodeForFloat() {
        return Opcodes.IFNE;
    }

}
