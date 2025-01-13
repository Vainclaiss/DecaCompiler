package fr.ensimag.deca.tree;

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

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        // Validate that left is a correct LValue, right is typed correctly
        Type leftType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        setRightOperand(
            getRightOperand().verifyRValue(compiler, localEnv, currentClass, leftType)
        );
        setType(leftType);
        return leftType;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        getRightOperand().codeExp(compiler, 2);
        
        compiler.addInstruction(new STORE(Register.getR(2), (DAddr)getLeftOperand().getDVal()));
    }

    @Override
    protected void codeGenByteInst(MethodVisitor mv) {
        getRightOperand().codeByteExp(mv);
    
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
