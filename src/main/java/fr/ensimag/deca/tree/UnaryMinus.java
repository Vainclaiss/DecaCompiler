package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.OPP;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * @author gl01
 * @date 01/01/2025
 */
public class UnaryMinus extends AbstractUnaryExpr {

    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }

    @Override
    protected Type getTypeUnaryOp(DecacCompiler compiler, Type type) throws ContextualError {
        if (type.isInt() || type.isFloat()) {
            return type;
        }

        throw new ContextualError("Error: Incompatible type for operator " + getOperatorName() + " and type " + type,
                getLocation());
    }
    
    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        getOperand().codeExp(compiler, n);
        compiler.addInstruction(new OPP(Register.getR(n), Register.getR(n)));
    }
    @Override
protected void codeByteExp(MethodVisitor mv) {
    getOperand().codeByteExp(mv);

    Type operandType = getOperand().getType();

    if (operandType.isInt()) {

        mv.visitInsn(Opcodes.INEG);

    } else if (operandType.isFloat()) {
        
        mv.visitInsn(Opcodes.FNEG);
    } else {
        throw new UnsupportedOperationException("UnaryMinus: Unsupported type for negation: " + operandType);
    }
}


    @Override
    protected String getOperatorName() {
        return "-";
    }

}
