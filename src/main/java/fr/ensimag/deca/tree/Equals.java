package fr.ensimag.deca.tree;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fr.ensimag.deca.context.Type;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;

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
    protected String getOperatorName() {
        return "==";
    }

    @Override
    protected void codeGenByteBool(MethodVisitor mv, boolean branchIfTrue, org.objectweb.asm.Label e,DecacCompiler compiler) {
        getLeftOperand().codeByteExp(mv,compiler);
    
        getRightOperand().codeByteExp(mv,compiler);
    
        Type leftType = getLeftOperand().getType();
        if (leftType.isInt()) {

            if (branchIfTrue) {
                mv.visitJumpInsn(Opcodes.IF_ICMPEQ, e);  // IF_ICMPEQ pour voir s'ils sont égaux qui est le cas
                                                        // puis on jump au label e
                                                       
            } else {
                mv.visitJumpInsn(Opcodes.IF_ICMPNE, e);// IF_ICMPNE pour voir qu'ils ne sont pas equals qui est le cas
                                                        // on jump au label e
            }
        } 
        else if (leftType.isFloat()) { // meme chose

            mv.visitInsn(Opcodes.FCMPG);
    
            if (branchIfTrue) {
                mv.visitJumpInsn(Opcodes.IFEQ, e);
            } else {
                mv.visitJumpInsn(Opcodes.IFNE, e);
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
