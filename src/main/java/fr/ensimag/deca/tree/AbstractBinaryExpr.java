package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ContextualError;
 import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.ima.pseudocode.BinaryInstruction;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Operand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;

/**
 * Binary expressions.
 *
 * @author gl01
 * @date 01/01/2025
 */
public abstract class AbstractBinaryExpr extends AbstractExpr {

    public AbstractExpr getLeftOperand() {
        return leftOperand;
    }

    public AbstractExpr getRightOperand() {
        return rightOperand;
    }

    protected void setLeftOperand(AbstractExpr leftOperand) {
        Validate.notNull(leftOperand);
        this.leftOperand = leftOperand;
    }

    protected void setRightOperand(AbstractExpr rightOperand) {
        Validate.notNull(rightOperand);
        this.rightOperand = rightOperand;
    }

    private AbstractExpr leftOperand;
    private AbstractExpr rightOperand;

    public AbstractBinaryExpr(AbstractExpr leftOperand,
            AbstractExpr rightOperand) {
        Validate.notNull(leftOperand, "left operand cannot be null");
        Validate.notNull(rightOperand, "right operand cannot be null");
        Validate.isTrue(leftOperand != rightOperand, "Sharing subtrees is forbidden");
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    protected void codeGenInst(DecacCompiler compiler, DVal op1, GPRegister r) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected DVal getDVal() {
        return null;
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        getLeftOperand().codeExp(compiler, n);
        Register.setRegistreLibre(n, false);
        DVal dvalExp2 = getRightOperand().getDVal();
        
        if (dvalExp2 == null) {
            if (n == Register.RMAX) {
                //sauvegarde de op1
                compiler.addInstruction(new PUSH(Register.getR(n)));

                //calcul de op2 dans R0
                getRightOperand().codeExp(compiler, n);
                Register.setRegistreLibre(n, false);
                compiler.addInstruction(new LOAD(Register.getR(n), Register.R0));

                //restoration de la valeur de op1 dans Rn
                compiler.addInstruction(new POP(Register.getR(n)));

                codeGenInst(compiler, Register.R0, Register.getR(n));
                Register.setRegistreLibre(n, true);
            }
            else { 
                getRightOperand().codeExp(compiler, n+1);
                Register.setRegistreLibre(n+1, false);
                
                codeGenInst(compiler, Register.getR(n+1), Register.getR(n));
                Register.setRegistreLibre(n+1, true);
            }
        }
        else {
            codeGenInst(compiler, dvalExp2, Register.getR(n));
        }
    }

    protected Type getTypeBinaryOp(DecacCompiler compiler, Type type1, Type t2) throws ContextualError {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        Type type1 = leftOperand.verifyExpr(compiler, localEnv, currentClass);
        Type type2 = rightOperand.verifyExpr(compiler, localEnv, currentClass);

        Type resType = getTypeBinaryOp(compiler, type1, type2);
        setType(resType);
        return resType;
    }


    @Override
    public void decompile(IndentPrintStream s) {
        getLeftOperand().decompile(s);
        if (getOperatorName().equals("=")) {
            s.print(" " + getOperatorName() + " ");
        } else {
            s.print(getOperatorName());
        }
        getRightOperand().decompile(s);
    }

    abstract protected String getOperatorName();


    @Override
    protected void iterChildren(TreeFunction f) {
        leftOperand.iter(f);
        rightOperand.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        leftOperand.prettyPrint(s, prefix, false);
        rightOperand.prettyPrint(s, prefix, true);
    }

}
