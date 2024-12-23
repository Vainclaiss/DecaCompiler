package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Unary expression.
 *
 * @author gl01
 * @date 01/01/2025
 */
public abstract class AbstractUnaryExpr extends AbstractExpr {

    public AbstractExpr getOperand() {
        return operand;
    }
    private AbstractExpr operand;
    public AbstractUnaryExpr(AbstractExpr operand) {
        Validate.notNull(operand);
        this.operand = operand;
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        // on charge la valeur de l'expression dans un registre libre
        int indexR = Register.getIndexRegistreLibre();
        codeExp(compiler, indexR);

        //on la met dans R1 pour l'afficher
        compiler.addInstruction(new LOAD(Register.getR(indexR), Register.R1));
        Register.setRegistreLibre(indexR, true);
    }

    protected Type getTypeUnaryOp(DecacCompiler compiler, Type type) throws ContextualError {
        if (type.isBoolean() || type.isInt() || type.isFloat()) {
            return type;
        }

        throw new ContextualError("Incompatible types for arithmetic operation: " + getOperatorName() + type, getLocation());
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        Type type = operand.verifyExpr(compiler, localEnv, currentClass);
        setType(type);
        
        return type;
    }

    protected abstract String getOperatorName();
  
    @Override
    public void decompile(IndentPrintStream s) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        operand.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        operand.prettyPrint(s, prefix, true);
    }

}
