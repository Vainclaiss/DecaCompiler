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
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.ParamDefinition;
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
        Type type1 = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        setRightOperand(getRightOperand().verifyRValue(compiler, localEnv, currentClass, type1));
        setType(type1);

        return type1;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        getRightOperand().codeExp(compiler, 2);
        getLeftOperand().codeExp(compiler);

        compiler.addInstruction(new STORE(Register.getR(compiler, 2), (DAddr) getLeftOperand().getDVal()));
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        getRightOperand().codeExp(compiler, n);
        getLeftOperand().codeExp(compiler);

        compiler.addInstruction(new STORE(Register.getR(compiler, n), (DAddr) getLeftOperand().getDVal()));
    }

    @Override
    protected void codeByteExp(MethodVisitor mv, DecacCompiler compiler) {
        getRightOperand().codeByteExp(mv, compiler);

        mv.visitInsn(Opcodes.DUP);

        if (!(getLeftOperand() instanceof Identifier)) {
            throw new DecacInternalError("Assign: left operand is not an Identifier.");
        }

        Identifier leftId = (Identifier) getLeftOperand();
        VariableDefinition varDef = leftId.getVariableDefinition();
        int localIndex = varDef.getLocalIndex();

        if (localIndex < 0) {
            throw new DecacInternalError("Variable local index not set before assignment.");
        }

        if (getType().isInt()) {
            mv.visitVarInsn(Opcodes.ISTORE, localIndex);
        } else if (getType().isFloat()) {
            mv.visitVarInsn(Opcodes.FSTORE, localIndex);
        } else if (getType().isBoolean()) {
            mv.visitVarInsn(Opcodes.ISTORE, localIndex);
        } else {
            throw new DecacInternalError("Unsupported type for assignment: " + getType());
        }

    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e) {
        codeGenInst(compiler);
        getLeftOperand().codeGenBool(compiler, branchIfTrue, e);
    }

    @Override
    protected void codeGenByteBool(MethodVisitor mv, boolean branchIfTrue, org.objectweb.asm.Label e,
            DecacCompiler compiler) {
        codeGenByteInst(mv, compiler);
        getLeftOperand().codeGenByteBool(mv, branchIfTrue, e, compiler);
    }

    @Override
    protected void codeGenByteInst(MethodVisitor mv, DecacCompiler compiler) {
        getRightOperand().codeByteExp(mv, compiler);

        AbstractExpr lhs = getLeftOperand();

        if (lhs instanceof Identifier) {
            Identifier leftId = (Identifier) lhs;

            if (leftId.getDefinition().isParam()) {
                // Handle method parameters
                ParamDefinition paramDef = (ParamDefinition) leftId.getDefinition();
                int index = paramDef.getIndexInLocalTable();

                if (getType().isInt() || getType().isBoolean()) {
                    mv.visitVarInsn(Opcodes.ISTORE, index);
                } else if (getType().isFloat()) {
                    mv.visitVarInsn(Opcodes.FSTORE, index);
                } else if (getType().isClass()) {
                    mv.visitVarInsn(Opcodes.ASTORE, index);
                } else {
                    throw new DecacInternalError("Unsupported param type for assignment: " + getType());
                }

            } else if (leftId.getDefinition().isVar()) {
                // Handle local variables
                VariableDefinition varDef = leftId.getVariableDefinition();
                int localIndex = varDef.getLocalIndex();

                if (localIndex < 0) {
                    throw new DecacInternalError("Variable local index not set before assignment.");
                }

                if (getType().isInt() || getType().isBoolean()) {
                    mv.visitVarInsn(Opcodes.ISTORE, localIndex);
                } else if (getType().isFloat()) {
                    mv.visitVarInsn(Opcodes.FSTORE, localIndex);
                } else if (getType().isClass()) {
                    mv.visitVarInsn(Opcodes.ASTORE, localIndex);
                } else {
                    throw new DecacInternalError("Unsupported type for assignment: " + getType());
                }

            } else if (leftId.getDefinition().isField()) {
                // Handle class fields
                FieldDefinition fieldDef = leftId.getFieldDefinition();
                String ownerInternalName = fieldDef.getContainingClass().getInternalName();
                String fieldName = leftId.getName().toString();
                String fieldDesc = getType().toJVMDescriptor();

                mv.visitVarInsn(Opcodes.ALOAD, 0); // Load "this" reference
                getRightOperand().codeByteExp(mv, compiler); // Evaluate right operand
                mv.visitFieldInsn(Opcodes.PUTFIELD, ownerInternalName, fieldName, fieldDesc);

            } else {
                throw new DecacInternalError("Unsupported identifier definition type for assignment.");
            }
        } else if (lhs instanceof Selection) {
            // Handle object field assignment via selection (e.g., obj.field = value)
            Selection selection = (Selection) lhs;

            selection.getLeftOperand().codeByteExp(mv, compiler);
            getRightOperand().codeByteExp(mv, compiler);

            FieldDefinition fd = selection.getRightOperand().getFieldDefinition();
            String ownerInternalName = fd.getContainingClass().getInternalName();
            String fieldName = selection.getRightOperand().getName().toString();
            String fieldDesc = getType().toJVMDescriptor();

            mv.visitFieldInsn(Opcodes.PUTFIELD, ownerInternalName, fieldName, fieldDesc);
        } else {
            throw new DecacInternalError("Assignment to an unsupported LHS type: " + lhs.getClass().getName());
        }
    }

    @Override
    protected String getOperatorName() {
        return "=";
    }
}
