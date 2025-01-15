package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.execerrors.OverflowError;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.MUL;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


/**
 * @author gl01
 * @date 01/01/2025
 */
public class Multiply extends AbstractOpArith {
    public Multiply(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    private int nextLocalIndex = 1;

    @Override
    protected void codeGenInst(DecacCompiler compiler, DVal op1, GPRegister r) {
        compiler.addInstruction(new MUL(op1, r));
        if (getType().isFloat() && !compiler.getCompilerOptions().getSkipExecErrors()) {
            compiler.addExecError(OverflowError.INSTANCE);
            compiler.addInstruction(new BOV(OverflowError.INSTANCE.getLabel()));
        }
    }


   
    /*
     * @Override
    protected void codeGenByteInst(MethodVisitor mv) {
        getLeftOperand().codeByteExp(mv);
    
        int leftVarIndex = allocateLocalIndex();
        if (getType().isInt()) {
            mv.visitVarInsn(Opcodes.ISTORE, leftVarIndex);
        } else if (getType().isFloat()) {
            mv.visitVarInsn(Opcodes.FSTORE, leftVarIndex);
        } else {
            throw new UnsupportedOperationException("Unsupported type for multiplication: " + getType());
        }
    
        getRightOperand().codeByteExp(mv);
    
        if (getType().isInt()) {
            mv.visitVarInsn(Opcodes.ILOAD, leftVarIndex);
            mv.visitInsn(Opcodes.IMUL);
        } else if (getType().isFloat()) {
            mv.visitVarInsn(Opcodes.FLOAD, leftVarIndex);
            mv.visitInsn(Opcodes.FMUL);
        }
    }
     */
    @Override
    protected void codeGenByteInst(MethodVisitor mv,DecacCompiler compiler) {
        getLeftOperand().codeByteExp(mv,compiler);
        
        getRightOperand().codeByteExp(mv,compiler);
        if (getType().isInt()) {
            mv.visitInsn(Opcodes.IMUL);
        } else {
            mv.visitInsn(Opcodes.FMUL);
        }
    }
    



    @Override
    protected String getOperatorName() {
        return "*";
    }



}
