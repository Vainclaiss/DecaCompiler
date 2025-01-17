package fr.ensimag.deca.tree;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BGT;
import fr.ensimag.ima.pseudocode.instructions.BLE;
import fr.ensimag.ima.pseudocode.instructions.SLE;

/**
 *
 * @author gl01
 * @date 01/01/2025
 */
public class LowerOrEqual extends AbstractOpIneq {
    public LowerOrEqual(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e) {
        codeExp(compiler, 2);
        if (branchIfTrue) {
            compiler.addInstruction(new BLE(e));
        } else {
            compiler.addInstruction(new BGT(e));
        }
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        super.codeExp(compiler, n);
        compiler.addInstruction(new SLE(Register.getR(compiler,n)));
    }

    @Override
    protected String getOperatorName() {
        return "<=";
    }
    @Override
    protected void codeGenByteBool(MethodVisitor mv, boolean branchIfTrue, org.objectweb.asm.Label e, DecacCompiler compiler) {
        getLeftOperand().codeByteExp(mv, compiler);
    
        getRightOperand().codeByteExp(mv, compiler);
    
        Type leftType = getLeftOperand().getType();
    
        if (leftType.isInt()) {
            
            if (branchIfTrue) {
                mv.visitJumpInsn(Opcodes.IF_ICMPLE, e);
            } else {
                mv.visitJumpInsn(Opcodes.IF_ICMPGT, e);
            }
        } else if (leftType.isFloat()) {
           
            mv.visitInsn(Opcodes.FCMPG); 
            if (branchIfTrue) {
                mv.visitJumpInsn(Opcodes.IFLE, e);
            } else {
                mv.visitJumpInsn(Opcodes.IFGT, e);
            }
        } else {
            throw new UnsupportedOperationException(
                    "LowerOrEqual codeGenByteBool: unhandled type for '<=' operation"
            );
        }
    }
    

    @Override
    protected int getJumpOpcodeForInt() {
        return Opcodes.IF_ICMPLE; 
    }

    @Override
    protected int getJumpOpcodeForFloat() {
        return Opcodes.IFLE;     
    }
    
}

