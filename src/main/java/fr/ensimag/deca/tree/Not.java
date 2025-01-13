package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl01
 * @date 01/01/2025
 */
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    protected Type getTypeUnaryOp(DecacCompiler compiler, Type type) throws ContextualError {
        if (type.isBoolean()) {
            return type;
        }

        throw new ContextualError("Error: Incompatible type for operator " + getOperatorName() + " and type " + type,
                getLocation());
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e) {
        getOperand().codeGenBool(compiler, !branchIfTrue, e);
    }

    @Override
    protected String getOperatorName() {
        return "!";
    }
}
