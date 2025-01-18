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
    protected void codeByteExp(MethodVisitor mv, DecacCompiler compiler) throws ContextualError  {
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
        }else if (getType().isBoolean()){
            mv.visitVarInsn(Opcodes.ISTORE,localIndex);
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
    protected void codeGenByteBool(MethodVisitor mv, boolean branchIfTrue, org.objectweb.asm.Label e,DecacCompiler compiler) throws ContextualError {
        codeGenByteInst(mv, compiler);
        getLeftOperand().codeGenByteBool(mv,branchIfTrue,e,compiler);
    }

    @Override
protected void codeGenByteInst(MethodVisitor mv, DecacCompiler compiler) throws ContextualError {
    // 1) Generate the right side expression => top of stack
    getRightOperand().codeByteExp(mv, compiler);

    // 2) Check the left operand type
    AbstractExpr lhs = getLeftOperand();

    if (lhs instanceof Identifier) {
        // Store into a local variable
        Identifier leftId = (Identifier) lhs;
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
    }
    else if (lhs instanceof Selection) {
        Selection selection = (Selection) lhs;
    
        // 1) Generate code for the *object* (left operand of selection)
        //    so the stack has [objectRef].
        selection.getLeftOperand().codeByteExp(mv, compiler);
    
        // 2) Null check if desired (similar to your getfield approach)
    
        // 3) Generate code for the *value* (the RHS)
        getRightOperand().codeByteExp(mv, compiler);
    
        // => Now the stack is [objectRef, value].
    
        // 4) Do PUTFIELD
        FieldDefinition fd = selection.getRightOperand().getFieldDefinition();
        String ownerInternalName = fd.getContainingClass().getInternalName();
        String fieldName = selection.getRightOperand().getName().toString();
        String fieldDesc = getType().toJVMDescriptor();
    
        // store => pop [objectRef, value]
        mv.visitFieldInsn(Opcodes.PUTFIELD, ownerInternalName, fieldName, fieldDesc);
    }
    
    
    else {
        throw new DecacInternalError("Assignment to an unsupported LHS type: " + lhs.getClass().getName());
    }
}

    
    @Override
    protected String getOperatorName() {
        return "=";
    }
}
