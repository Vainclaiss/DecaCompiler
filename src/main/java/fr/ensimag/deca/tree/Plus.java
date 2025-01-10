package fr.ensimag.deca.tree;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.ADD;


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
    }

     @Override
    protected void codeGenByteInst(MethodVisitor mv) {
        getLeftOperand().codeGenByteInst(mv);
        getRightOperand().codeGenByteInst(mv);

        if (getType().isInt()) {

            mv.visitInsn(Opcodes.IADD);

        } else if (getType().isFloat()) {

            mv.visitInsn(Opcodes.FADD);
            
        } else {
            throw new DecacInternalError(
                "Plus: unsupported type for addition: " + getType());
        }
    }

    @Override
    protected String getOperatorName() {
        return "+";
    }
}
