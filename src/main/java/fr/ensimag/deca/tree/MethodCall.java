package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.execerrors.IOError;
import fr.ensimag.deca.codegen.execerrors.NullDereference;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.RFLOAT;
import fr.ensimag.ima.pseudocode.instructions.RINT;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.SUB;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;

import java.beans.MethodDescriptor;
import java.io.PrintStream;
import java.util.List;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodCall extends AbstractExpr {

    private final AbstractExpr leftOperand;
    private final AbstractIdentifier methodName;
    private final ListExpr rightOperand;

    public MethodCall(AbstractExpr leftOperand, AbstractIdentifier methodName, ListExpr rightOperand) {
        this.leftOperand = leftOperand;
        this.methodName = methodName;
        this.rightOperand = rightOperand;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.debug("Verifying left operand expression");
        Type classType = leftOperand.verifyExpr(compiler, localEnv, currentClass);
        if (!classType.isClass()) {
            LOG.error("Left operand of a selection is not a class");
            throw new ContextualError("Error: Left operand of a selection must be a class", getLocation());
        }

        LOG.debug("Verifying class type");
        ClassDefinition class2 = classType.asClassType("Error: Cast failed from Type to ClassType", getLocation())
                .getDefinition();
        EnvironmentExp envExp2 = class2.getMembers();
        MethodDefinition methodDef = methodName.verifyMethod(envExp2);

        LOG.debug("Verifying method arguments");
        rightOperand.verifyRValueStar(compiler, localEnv, currentClass, methodDef.getSignature());

        LOG.debug("Setting return type of the method");
        setType(methodDef.getType());
        return methodDef.getType();
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        // TODO : gerer le cas de this
        String methodeLabel = methodName.getMethodDefinition().getLabel().toString().replace("code.", "");
        compiler.addComment("Empilement des arguments de " + methodeLabel);
        compiler.addInstruction(new ADDSP(rightOperand.size() + 1));
        // TODO : c'est frauduleux !!! il faut gerer tout type d'exp
        leftOperand.codeExp(compiler, 0); // method call, new, selection, variables in R0
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(0, Register.SP)));

        List<AbstractExpr> params = rightOperand.getList();
        compiler.getStackOverflowCounter().addParamsOnStack(params.size());
        for (int i = params.size(); i > 0; i--) {
            params.get(i - 1).codeExp(compiler, n); // TODO : checker le numero de registre
            compiler.addInstruction(new STORE(Register.getR(compiler, n), new RegisterOffset(-i, Register.SP)));
        }

        compiler.addComment("Appel de la methode " + methodeLabel);
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.SP), Register.R0));

        if (!compiler.getCompilerOptions().getSkipExecErrors()) {
            compiler.addExecError(NullDereference.INSTANCE);
            compiler.addInstruction(new CMP(new NullOperand(), Register.R0));
            compiler.addInstruction(new BEQ(NullDereference.INSTANCE.getLabel()));
        }

        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.R0), Register.R0));
        compiler.addInstruction(new BSR(new RegisterOffset(methodName.getMethodDefinition().getIndex(), Register.R0)));
        compiler.addInstruction(new SUBSP(rightOperand.size() + 1));
        compiler.addComment("fin appel de methode");
        compiler.addInstruction(new LOAD(Register.R0, Register.getR(compiler, n)));
    }

    @Override
protected void codeByteExp(MethodVisitor mv, DecacCompiler compiler) throws ContextualError {
    // 1) Evaluate the left operand (object), pushing it on the stack.
    leftOperand.codeByteExp(mv, compiler);

    // 2) Optional Null Check:
    //    If you want to replicate your custom null dereference error,
    //    you can do something like:
    org.objectweb.asm.Label notNullLabel = new org.objectweb.asm.Label();
    mv.visitInsn(Opcodes.DUP);
    mv.visitJumpInsn(Opcodes.IFNONNULL, notNullLabel);
    // If null => throw a NullPointerException, or jump to some error handler
    mv.visitTypeInsn(Opcodes.NEW, "java/lang/NullPointerException");
    mv.visitInsn(Opcodes.DUP);
    mv.visitMethodInsn(
        Opcodes.INVOKESPECIAL,
        "java/lang/NullPointerException",
        "<init>",
        "()V",
        false
    );
    mv.visitInsn(Opcodes.ATHROW);
    mv.visitLabel(notNullLabel);

    // 3) Evaluate each argument and push it on the stack in order.
    //    The JVM calls expect arguments in left-to-right order.
    for (AbstractExpr arg : rightOperand.getList()) {
        arg.codeByteExp(mv, compiler);
        // If your method expects int => push int. 
        // If float => you push float. 
        // etc., the codeByteExp must handle that.
    }

    // 4) INVOKEVIRTUAL 
    //    Build the internal name for the owner class, method name, and descriptor 
    //    from the method definition (type, signature).
    MethodDefinition methodDef = methodName.getMethodDefinition();
    // Suppose we have the internal name of the class from the left operand's type
Type leftType = leftOperand.getType();

// 1) Check for class type
if (!leftType.isClass()) {
    // If you want to “skip the error,” 
    // do whatever fallback or log you want here:
    // e.g. just return or handle gracefully
    return;
}

// 2) Cast to ClassType
ClassType ctype = (ClassType) leftType;
ClassDefinition classDef = ctype.getDefinition();

// Now do whatever you need with classDef...
    String ownerInternalName = classDef.getInternalName(); 
    // or something like: classDef.getType().getName().toString().replace('.', '/');

    // For the method name:
    String jvmMethodName = methodName.getName().toString(); // e.g. "foo"

    // For the descriptor, build from methodDef.getSignature() 
    // e.g. (IF)V if method takes (int, float) and returns void
    String descriptor = DeclMethod.buildMethodDescriptor(methodDef.getSignature(), methodDef.getType());

    // Finally, call it
    mv.visitMethodInsn(
        Opcodes.INVOKEVIRTUAL, 
        ownerInternalName, 
        jvmMethodName, 
        descriptor, 
        false
    );

    // Now if the method returns something non-void, that result is on the stack.
    // If it's void, the top of the stack is empty for the method result.
}

    @Override
    public DVal getDVal() {
        return Register.R0;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        codeExp(compiler);
    }

    @Override
protected void codeGenByteInst(MethodVisitor mv, DecacCompiler compiler) throws ContextualError {
    // Delegate the actual bytecode generation to codeByteExp
    codeByteExp(mv, compiler);

    // If the method returns a value and it's used in an expression, 
    // the result remains on the stack. For an instruction context, we may need to pop it.
    if (!this.getType().isVoid()) {
        // If the method has a return type (e.g., int, float, object), and it's 
        // called in a statement context, discard the result with a POP.
        mv.visitInsn(Opcodes.POP);
    }
}




    @Override
    protected void codeExp(DecacCompiler compiler) {
        codeExp(compiler, 2); // TODO : vraiment pas optimiser mais sinon possible erreur
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e) {
        codeExp(compiler, 2);
        compiler.addInstruction(new CMP(1, Register.R0));
        if (branchIfTrue) {
            compiler.addInstruction(new BEQ(e));
        } else {
            compiler.addInstruction(new BNE(e));
        }
    }

    @Override
protected void codeGenByteBool(MethodVisitor mv, boolean branchIfTrue, org.objectweb.asm.Label e, DecacCompiler compiler) throws ContextualError {
    // Generate bytecode for left operand (the object)
    leftOperand.codeByteExp(mv, compiler);

    // Generate bytecode for method arguments
    for (AbstractExpr arg : rightOperand.getList()) {
        arg.codeByteExp(mv, compiler);
    }

    // Get the method definition
    MethodDefinition methodDef = methodName.getMethodDefinition();
    Type leftType = leftOperand.getType();
    ClassType ctype = leftType.asClassType(
        "Cannot cast left operand to ClassType",
        leftOperand.getLocation()
    );
    String ownerInternalName = ctype.getDefinition().getInternalName();
    
    // Determine the owner class and method descriptor
    String methodNameStr = methodName.getName().toString();
    String methodDescriptor = DeclMethod.buildMethodDescriptor(methodDef.getSignature(), methodDef.getType());

    // Call the method
    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, ownerInternalName, methodNameStr, methodDescriptor, false);

    // If boolean, branch based on the result
    if (methodDef.getType().isBoolean()) {
        if (branchIfTrue) {
            mv.visitJumpInsn(Opcodes.IFNE, e); // Branch if true (non-zero result)
        } else {
            mv.visitJumpInsn(Opcodes.IFEQ, e); // Branch if false (zero result)
        }
    } else {
        throw new UnsupportedOperationException("MethodCall: Expected boolean return type for codeGenByteBool");
    }
}


    @Override
    public void decompile(IndentPrintStream s) {
        leftOperand.decompile(s);
        s.print(".");
        methodName.decompile(s);
        s.print("(");
        rightOperand.decompile(s);
        s.print(")");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        leftOperand.prettyPrint(s, prefix, false);
        methodName.prettyPrint(s, prefix, false);
        rightOperand.prettyPrint(s, prefix, false);
    }

}
