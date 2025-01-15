package fr.ensimag.deca.tree;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.execerrors.OverflowError;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.SUB;

/**
 * @author gl01
 * @date 01/01/2025
 */
public class Minus extends AbstractOpArith {
    public Minus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    private int nextLocalIndex = 1;

    @Override
    protected void codeGenInst(DecacCompiler compiler, DVal op1, GPRegister r) {
        compiler.addInstruction(new SUB(op1, r));
        if (getType().isFloat() && !compiler.getCompilerOptions().getSkipExecErrors()) {
            compiler.addExecError(OverflowError.INSTANCE);
            compiler.addInstruction(new BOV(OverflowError.INSTANCE.getLabel()));
        }
    }

    @Override
    protected String getOperatorName() {
        return "-";
    }

    protected int allocateLocalIndex() {
        return nextLocalIndex++;
    }

    /*
     * 
     * @Override
    protected void codeGenByteInst(MethodVisitor mv) {
        getLeftOperand().codeByteExp(mv);
        int leftVarIndex = allocateLocalIndex(); 
        mv.visitVarInsn(Opcodes.ISTORE, leftVarIndex); 
    
        getRightOperand().codeByteExp(mv);
    
        mv.visitVarInsn(Opcodes.ILOAD, leftVarIndex);
    
        if (getType().isInt()) {
            mv.visitInsn(Opcodes.ISUB);
        } else if (getType().isFloat()) {
            mv.visitInsn(Opcodes.FSUB);
        } else {
            throw new UnsupportedOperationException(
                "Subtraction: unsupported type: " + getType()
            );
        }
    }
     * 
     */
    @Override
    protected void codeGenByteInst(MethodVisitor mv,DecacCompiler compiler) {
        getLeftOperand().codeByteExp(mv,compiler);
    
        getRightOperand().codeByteExp(mv,compiler);
    
    
        if (getType().isInt()) {
            mv.visitInsn(Opcodes.ISUB);
        } else if (getType().isFloat()) {
            mv.visitInsn(Opcodes.FSUB);
        } else {
            throw new UnsupportedOperationException(
                "Subtraction: unsupported type: " + getType()
            );
        }
    }
    
    

    
}
