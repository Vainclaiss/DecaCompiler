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
    
}
