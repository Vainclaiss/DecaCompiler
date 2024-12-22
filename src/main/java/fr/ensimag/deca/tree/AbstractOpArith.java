package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Arithmetic binary operations (+, -, /, ...)
 * 
 * @author gl01
 * @date 01/01/2025
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {

    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        // on charge la valeur de l'expression dans un registre libre
        int indexR = Register.getIndexRegistreLibre();
        codeExp(compiler, indexR);

        //on la met dans R1 pour l'afficher
        compiler.addInstruction(new LOAD(Register.getR(indexR), Register.R1));
        Register.setRegistreLibre(indexR, true);
        compiler.addInstruction(new WINT());
    }

    @Override
    protected Type getTypeBinaryOp(DecacCompiler compiler, Type type1, Type type2) throws ContextualError {
        if (type1.isInt() && type2.isInt()) {
            return compiler.environmentType.INT;
        }

        if (type1.isInt() && type2.isFloat()) {
            return compiler.environmentType.FLOAT;
        }

        if (type1.isFloat() && type2.isInt()) {
            return compiler.environmentType.FLOAT;
        }

        if (type1.isFloat() && type2.isFloat()) {
            return compiler.environmentType.FLOAT;
        }

        throw new ContextualError("Incompatible types for arithmetic operation: " + type1 + getOperatorName() + type2, getLocation());
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
