package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Type;

/**
 *
 * @author gl01
 * @date 01/01/2025
 */
public abstract class AbstractOpExactCmp extends AbstractOpCmp {

    public AbstractOpExactCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected Type getTypeBinaryOp(DecacCompiler compiler, Type type1, Type type2) throws ContextualError {
        if ((type1.isBoolean() && type2.isBoolean()) || (type1.isClassOrNull() && type2.isClassOrNull())) {
            return compiler.environmentType.BOOLEAN;
        }
        return super.getTypeBinaryOp(compiler, type1, type2);
    }
    
}
