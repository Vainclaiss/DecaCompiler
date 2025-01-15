package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.objectweb.asm.MethodVisitor;

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
        codeExp(compiler, 2);

        //on la met dans R1 pour l'afficher
        compiler.addInstruction(new LOAD(Register.getR(2), Register.R1));
    }

    @Override
    protected void codeGenBytePrint(MethodVisitor mv,DecacCompiler compiler){
        throw new UnsupportedOperationException("not yet implemented");
    }

    protected Type getTypeUnaryOp(DecacCompiler compiler, Type type) throws ContextualError {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        Type type1 = operand.verifyExpr(compiler, localEnv, currentClass);
        Type type = getTypeUnaryOp(compiler, type1);
        setType(type);
        
        return type;
    }

    @Override
    protected DVal getDVal() {
        return null;
    }

    protected abstract String getOperatorName();
  
    @Override
    public void decompile(IndentPrintStream s) {
        s.print(getOperatorName() + operand.decompile());
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
