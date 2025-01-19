package fr.ensimag.deca.tree;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.SNE;

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
    protected void codeExp(DecacCompiler compiler, int n) {
        super.codeExp(compiler, n);
        compiler.addInstruction(new SNE(Register.getR(compiler,n)));
    }

    @Override
    protected void codeGenByteBool(MethodVisitor mv, boolean branchIfTrue, org.objectweb.asm.Label e, DecacCompiler compiler)
            throws ContextualError {
    
        getLeftOperand().codeByteExp(mv, compiler);
        getRightOperand().codeByteExp(mv, compiler);

        Type leftType = getLeftOperand().getType();

    
        if (leftType.isInt() || leftType.isBoolean()) {
            if (branchIfTrue) {
                mv.visitJumpInsn(Opcodes.IF_ICMPNE, e);
            } else {
                mv.visitJumpInsn(Opcodes.IF_ICMPEQ, e);
            }
        } else if (leftType.isFloat()) {
            mv.visitInsn(Opcodes.FCMPG);
            if (branchIfTrue) {
                mv.visitJumpInsn(Opcodes.IFNE, e);
            } else {
                mv.visitJumpInsn(Opcodes.IFEQ, e);
            }
        } 
        else if (leftType.isClass() || leftType.isNull()) {
          
            if (branchIfTrue) {
                mv.visitJumpInsn(Opcodes.IF_ACMPNE, e);
            } else {
                mv.visitJumpInsn(Opcodes.IF_ACMPEQ, e);
            }
        } else {
            throw new DecacInternalError(
              "NotEquals: Unsupported type for '!=' operator: " + getType());
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
