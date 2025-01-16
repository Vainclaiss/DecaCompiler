package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl01
 * @date 01/01/2025
 */
public class Assign extends AbstractBinaryExpr {

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue) super.getLeftOperand();
    }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        // A FAIRE pour les classes : lvalue verif
        Type type1 = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        setRightOperand(getRightOperand().verifyRValue(compiler, localEnv, currentClass, type1));
        setType(type1);
        
        return type1;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        // TODO : gerer les selections et le cas de this
        // TODO : gerer assign en tant qu'exp
        getRightOperand().codeExp(compiler, 2);
        getLeftOperand().codeExp(compiler);
        
        compiler.addInstruction(new STORE(Register.getR(compiler,2), (DAddr)getLeftOperand().getDVal()));
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        getLeftOperand().codeExp(compiler);
        getRightOperand().codeExp(compiler, n);
        
        compiler.addInstruction(new STORE(Register.getR(compiler,n), (DAddr)getLeftOperand().getDVal()));
    }

    @Override
    protected String getOperatorName() {
        return "=";
    }

}
