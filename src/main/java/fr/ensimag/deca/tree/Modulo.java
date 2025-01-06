package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.REM;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl01
 * @date 01/01/2025
 */
public class Modulo extends AbstractOpArith {

    public Modulo(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler, DVal op1, GPRegister r) {
        compiler.addInstruction(new REM(op1, r));
    }

    @Override
    protected Type getTypeBinaryOp(DecacCompiler compiler, Type type1, Type type2) throws ContextualError {
        if (type1.isInt() && type2.isInt()) {
            return type1;
        }

        throw new ContextualError(
                "Error: Incompatible types for arithmetic operation: " + type1 + getOperatorName() + type2,
                getLocation());
    }

    @Override
    protected String getOperatorName() {
        return "%";
    }

}
