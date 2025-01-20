package fr.ensimag.deca.tree;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BGE;
import fr.ensimag.ima.pseudocode.instructions.BLT;
import fr.ensimag.ima.pseudocode.instructions.SLT;

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
        codeExp(compiler, 2);
        if (branchIfTrue) {
            compiler.addInstruction(new BLT(e));
        } else {
            compiler.addInstruction(new BGE(e));
        }
    }

    @Override
    protected void codeGenByteBool(MethodVisitor mv, boolean branchIfTrue, 
                                   org.objectweb.asm.Label target, 
                                   DecacCompiler compiler)  {

        getLeftOperand().codeByteExp(mv, compiler);
    
        getRightOperand().codeByteExp(mv, compiler);
    
        Type leftType = getLeftOperand().getType();

        if (leftType.isInt()) {
            if (branchIfTrue) {
                mv.visitJumpInsn(Opcodes.IF_ICMPLT, target);  
            } else {
                mv.visitJumpInsn(Opcodes.IF_ICMPGE, target); 
            }
        }

        else if (leftType.isFloat()) {
         
            mv.visitInsn(Opcodes.FCMPL);  
            if (branchIfTrue) {
                mv.visitJumpInsn(Opcodes.IFLT, target);
            } else {
                mv.visitJumpInsn(Opcodes.IFGE, target);
            }
        } 
        else {
            throw new UnsupportedOperationException("Lower: unsupported operand types for '<'");
        }
    }
    
    
    protected void codeExp(DecacCompiler compiler, int n) {
        super.codeExp(compiler, n);
        compiler.addInstruction(new SLT(Register.getR(compiler,n)));
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
