package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

/**
 *
 * @author gl01
 * @date 01/01/2025
 */
public abstract class AbstractOpBool extends AbstractBinaryExpr {

    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected Type getTypeBinaryOp(DecacCompiler compiler, Type type1, Type type2) throws ContextualError {
        if (type1.isBoolean() && type2.isBoolean()) {
            return compiler.environmentType.BOOLEAN;
        }

        throw new ContextualError(
                "Error: Incompatible types for boolean operation: " + type1 + " " + getOperatorName() + " " + type2,
                getLocation());
    }

    // @Override
    // public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
    // ClassDefinition currentClass) throws ContextualError {
    // throw new UnsupportedOperationException("not yet implemented");
    // }

}
