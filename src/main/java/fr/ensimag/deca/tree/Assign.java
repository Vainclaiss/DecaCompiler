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
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Assignment, i.e. lvalue = expr.
 */
public class Assign extends AbstractBinaryExpr {

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction
        return (AbstractLValue) super.getLeftOperand();
    }

        // TODO: pour les classes : lvalue verif
        @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
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
    protected void codeGenByteInst(MethodVisitor mv,DecacCompiler compiler) {
        getRightOperand().codeByteExp(mv,compiler);
    
        if (!(getLeftOperand() instanceof Identifier)) {
            throw new DecacInternalError("Assign: left operand is not an Identifier.");
        }
        Identifier leftId = (Identifier) getLeftOperand();
        VariableDefinition varDef = leftId.getVariableDefinition();
    
        int localIndex = varDef.getLocalIndex();
        System.out.println(localIndex);
        if (localIndex < 0) {
            throw new DecacInternalError("Variable local index not set before assignment.");
        }
    
        // Use the correct store instruction
        if (getType().isInt()) {
            mv.visitVarInsn(Opcodes.ISTORE, localIndex);
            System.out.println("i am int");
        } else if (getType().isFloat()) {
            mv.visitVarInsn(Opcodes.FSTORE, localIndex);
        } else {
            throw new DecacInternalError("Unsupported type for assignment: " + getType());
        }
    }
    
    @Override
    protected String getOperatorName() {
        return "=";
    }
}
