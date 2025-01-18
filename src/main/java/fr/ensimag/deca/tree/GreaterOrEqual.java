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
import fr.ensimag.ima.pseudocode.instructions.SGE;

/**
 * Operator "x >= y"
 * 
 * @author gl01
 * @date 01/01/2025
 */
public class GreaterOrEqual extends AbstractOpIneq {

    public GreaterOrEqual(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e) {
        codeExp(compiler, 2);
        if (branchIfTrue) {
            compiler.addInstruction(new BGE(e));
        }
        else {
            compiler.addInstruction(new BLT(e));
        }
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        super.codeExp(compiler, n);
        compiler.addInstruction(new SGE(Register.getR(compiler,n)));
    }

    @Override
    protected String getOperatorName() {
        return ">=";
    }


    @Override
protected void codeGenByteBool(MethodVisitor mv, boolean branchIfTrue, org.objectweb.asm.Label e,DecacCompiler compiler) throws ContextualError {
    // on genere le bytecode du left operand et on le push sur le stack
    getLeftOperand().codeByteExp(mv,compiler);
    // on genere le bytecode du right operand et on le push sur le stack
    getRightOperand().codeByteExp(mv,compiler);

    
    Type leftType = getLeftOperand().getType();
    if (leftType.isInt()) {
   
        if (branchIfTrue) {
            mv.visitJumpInsn(Opcodes.IF_ICMPGE, e);
        } else {
            mv.visitJumpInsn(Opcodes.IF_ICMPLT, e);
        }
    }
    else if (leftType.isFloat()) {

        mv.visitInsn(Opcodes.FCMPG);

        if (branchIfTrue) {
            mv.visitJumpInsn(Opcodes.IFGE, e);
        } else {
            mv.visitJumpInsn(Opcodes.IFLT, e);
        }
    }
    else {
        throw new UnsupportedOperationException(
            "GreaterOrEqual codeGenByteBool: unhandled type for '>=' operation"
        );
    }
}
@Override
protected int getJumpOpcodeForInt() {
    return Opcodes.IF_ICMPGE;
}

@Override
protected int getJumpOpcodeForFloat() {
    return Opcodes.IFGE;
}


}
