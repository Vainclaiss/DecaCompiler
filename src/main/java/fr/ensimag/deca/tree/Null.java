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
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;

import java.io.PrintStream;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fr.ensimag.deca.context.NullType;

public class Null extends AbstractExpr{

    public Null() {
        this.dVal = new NullOperand();
    }

    private DVal dVal;

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        setType(compiler.environmentType.NULL);
        return compiler.environmentType.NULL;
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    protected DVal getDVal() {
        return dVal;
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        compiler.addInstruction(new LOAD(getDVal() , Register.getR(compiler,n)));
    }

    @Override
    protected void codeByteExp(MethodVisitor mv, DecacCompiler compiler)  {
        mv.visitInsn(Opcodes.ACONST_NULL);
    }
    



    @Override
    String prettyPrintNode() {
        return "Null";
    }
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("null");
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
