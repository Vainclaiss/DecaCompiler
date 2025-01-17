package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WSTR;

import java.io.PrintStream;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 *
 * @author gl01
 * @date 01/01/2025
 */
public class BooleanLiteral extends AbstractExpr {

    private boolean value;
    private ImmediateInteger dVal;

    public BooleanLiteral(boolean value) {
        this.value = value;
        this.dVal = new ImmediateInteger(value ? 1 : 0);
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        setType(compiler.environmentType.BOOLEAN);
        return compiler.environmentType.BOOLEAN;
    }

    @Override
    protected DVal getDVal() {
        return dVal;
    }

    @Override
    protected void codeExp(DecacCompiler compiler,int n) {
        compiler.addInstruction(new LOAD(dVal, Register.getR(compiler,n)));
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e) {
        if ((value && branchIfTrue) || (!value && !branchIfTrue)) {
            compiler.addInstruction(new BRA(e));
        }
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        compiler.addInstruction((value) ? new WSTR("true") : new WSTR("false"));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Boolean.toString(value));
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    String prettyPrintNode() {
        return "BooleanLiteral (" + value + ")";
    }

    @Override
    protected void codeGenBytePrint(MethodVisitor mv,DecacCompiler compiler) {
        mv.visitFieldInsn(
            org.objectweb.asm.Opcodes.GETSTATIC,
            "java/lang/System",
            "out",
            "Ljava/io/PrintStream;"
        );
    
        mv.visitLdcInsn(value ? "true" : "false");
    
        mv.visitMethodInsn(
            org.objectweb.asm.Opcodes.INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",          // or "print"
            "(Ljava/lang/String;)V",
            false
        );
    }

    @Override
protected void codeGenByteBool(MethodVisitor mv, boolean branchIfTrue, org.objectweb.asm.Label e,DecacCompiler compilr) {
    if ((value && branchIfTrue) || (!value && !branchIfTrue)) {
        mv.visitJumpInsn(Opcodes.GOTO, e);
    }
}

    
}
