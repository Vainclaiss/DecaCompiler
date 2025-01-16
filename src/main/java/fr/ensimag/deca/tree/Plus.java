package fr.ensimag.deca.tree;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.execerrors.IOError;
import fr.ensimag.deca.codegen.execerrors.OverflowError;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.BOV;


/**
 * @author gl01
 * @date 01/01/2025
 */
public class Plus extends AbstractOpArith {
    public Plus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
 

    @Override
    protected void codeGenInst(DecacCompiler compiler, DVal op1, GPRegister r) {
        compiler.addInstruction(new ADD(op1, r));
        if (getType().isFloat() && !compiler.getCompilerOptions().getSkipExecErrors()) {
            compiler.addExecError(OverflowError.INSTANCE);
            compiler.addInstruction(new BOV(OverflowError.INSTANCE.getLabel()));
        }
    }

@Override
protected void codeGenByteInst(MethodVisitor mv, DecacCompiler compiler) {
    getLeftOperand().codeByteExp(mv, compiler);
    int leftVarIndex = compiler.allocateLocalIndex();

    if (getType().isInt()) {
        mv.visitVarInsn(Opcodes.ISTORE, leftVarIndex);
    } else if (getType().isFloat()) {
        mv.visitVarInsn(Opcodes.FSTORE, leftVarIndex);
    } else {
        throw new UnsupportedOperationException("Unsupported type: " + getType());
    }

    
    getRightOperand().codeByteExp(mv, compiler);

    if (getType().isInt()) {
        mv.visitVarInsn(Opcodes.ILOAD, leftVarIndex);
        mv.visitInsn(Opcodes.IADD);
    } else {
        mv.visitVarInsn(Opcodes.FLOAD, leftVarIndex);
        mv.visitInsn(Opcodes.FADD);
    }
}


    @Override
    protected String getOperatorName() {
        return "+";
    }
}
