package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.ADD;
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

    @Override
    protected void codeGenInst(DecacCompiler compiler, DVal op1, GPRegister r) {
        compiler.addInstruction(new MUL(op1, r));
    }


    @Override
    protected void codeByteExp(MethodVisitor mv) {
        getLeftOperand().codeByteExp(mv);
        getRightOperand().codeByteExp(mv);
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
