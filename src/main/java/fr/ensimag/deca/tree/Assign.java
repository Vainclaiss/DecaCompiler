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
        // IMA code generation
        int indexR = Register.getIndexRegistreLibre();
        Register.setRegistreLibre(indexR, false);

        getRightOperand().codeExp(compiler, indexR);   // Evaluate right -> R(indexR)
        DAddr leftAddress = (DAddr) getLeftOperand().getDVal(); // LValue => memory
        compiler.addInstruction(new STORE(Register.getR(indexR), leftAddress));

        Register.setRegistreLibre(indexR, true);
    }

    @Override
    protected void codeGenByteInst(MethodVisitor mv) {
        // Generate code for the right-hand side (e.g., counter - 1)
        getRightOperand().codeByteExp(mv);
    
        // Get the variable definition for the left-hand side (e.g., `counter`)
        if (!(getLeftOperand() instanceof Identifier)) {
            throw new DecacInternalError("Assign: left operand is not an Identifier.");
        }
        Identifier leftId = (Identifier) getLeftOperand();
        VariableDefinition varDef = leftId.getVariableDefinition();
    
        // Store the result in the correct local index
        int localIndex = varDef.getLocalIndex();
        if (localIndex < 0) {
            throw new DecacInternalError("Variable local index not set before assignment.");
        }
    
        // Use the correct store instruction
        if (getType().isInt()) {
            mv.visitVarInsn(Opcodes.ISTORE, localIndex);
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
