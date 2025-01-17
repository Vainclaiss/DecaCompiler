package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
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

        // TODO: pour les classes : lvalue verif
        Type type1 = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        setRightOperand(getRightOperand().verifyRValue(compiler, localEnv, currentClass, type1));
        setType(type1);
        
        return type1;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {

        // TODO : ordre pas incroyable, on peut eventuellement perdre la valeur stocké dans 2, faire un push ??
        getRightOperand().codeExp(compiler, 2);
        getLeftOperand().codeExp(compiler);
        
        compiler.addInstruction(new STORE(Register.getR(compiler,2), (DAddr)getLeftOperand().getDVal()));
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        
        // TODO : ordre pas incroyable, on peut eventuellement perdre la valeur stocké dans 2, faire un push ??
        getRightOperand().codeExp(compiler, n);
        getLeftOperand().codeExp(compiler);
        
        compiler.addInstruction(new STORE(Register.getR(compiler,n), (DAddr)getLeftOperand().getDVal()));
    }
    
    @Override
    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e) {
        codeGenInst(compiler);
        getLeftOperand().codeGenBool(compiler, branchIfTrue, e);
    }

    @Override
    protected String getOperatorName() {
        return "=";
    }

}
