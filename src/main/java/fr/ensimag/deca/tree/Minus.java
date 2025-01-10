package fr.ensimag.deca.tree;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.SUB;

/**
 * @author gl01
 * @date 01/01/2025
 */
public class Minus extends AbstractOpArith {
    public Minus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler, DVal op1, GPRegister r) {
        compiler.addInstruction(new SUB(op1, r));
    }

    @Override
    protected String getOperatorName() {
        return "-";
    }

         @Override
    protected void codeGenByteInst(MethodVisitor mv) {
        getLeftOperand().codeGenByteInst(mv);
        getRightOperand().codeGenByteInst(mv);

        if (getType().isInt()) {

            mv.visitInsn(Opcodes.ISUB);

        } else if (getType().isFloat()) {

            mv.visitInsn(Opcodes.FSUB);
            
        } else {
            throw new DecacInternalError(
                "Plus: unsupported type for substraction: " + getType());
        }
    }

    
}
