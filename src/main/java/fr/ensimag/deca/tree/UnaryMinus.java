package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.OPP;
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

        throw new ContextualError("Incompatible type for operator " + getOperatorName() + " and type " + type, getLocation());
    }

    @Override
    protected void codeExp(DecacCompiler compiler,int n) {
        getOperand().codeExp(compiler, n);
        compiler.addInstruction(new OPP(Register.getR(n), Register.getR(n)));
    }


    @Override
    protected String getOperatorName() {
        return "-";
    }

}
