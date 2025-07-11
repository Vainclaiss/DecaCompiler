package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.Register;

import java.io.PrintStream;

import org.objectweb.asm.MethodVisitor;

/**
 * Integer literal
 *
 * @author gl01
 * @date 01/01/2025
 */
public class IntLiteral extends AbstractExpr {
    public int getValue() {
        return value;
    }

    @Override
    protected DVal getDVal() {
        return dVal;
    }

    private int value;
    private ImmediateInteger dVal;

    public IntLiteral(int value) {
        this.value = value;
        this.dVal = new ImmediateInteger(value);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        setType(compiler.environmentType.INT);
        return compiler.environmentType.INT;
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        compiler.addInstruction(new LOAD(dVal, Register.getR(compiler,n)));
    }

    /* 
    @Override
protected void codeGenBytePrint(MethodVisitor mv,DecacCompiler compiler) {
    mv.visitLdcInsn(value);

    mv.visitFieldInsn(org.objectweb.asm.Opcodes.GETSTATIC, 
        "java/lang/System", "out", "Ljava/io/PrintStream;");

    mv.visitInsn(org.objectweb.asm.Opcodes.SWAP);

    mv.visitMethodInsn(org.objectweb.asm.Opcodes.INVOKEVIRTUAL, 
        "java/io/PrintStream", "println", "(I)V", false);
}
        */


    @Override
    protected void codeByteExp(MethodVisitor mv,DecacCompiler compiler) {
 
        mv.visitLdcInsn(value);
    }
    

    

    @Override
    String prettyPrintNode() {
        return "Int (" + getValue() + ")";
    }
    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Integer.toString(value));
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

}
