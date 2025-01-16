package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.execerrors.IOError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.RFLOAT;
import fr.ensimag.ima.pseudocode.instructions.RINT;

import java.io.PrintStream;

public class This extends AbstractExpr {

    final private boolean impl;
    final private DVal dVal;

    public This(Boolean impl) {
        this.impl = impl;
        this.dVal = new RegisterOffset(-2, Register.LB);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        if (currentClass == null) {
            throw new ContextualError("Error: Illegal use of 'this' in main block", getLocation());
        }

        this.setType(currentClass.getType());
        return currentClass.getType();
    }
        
    @Override
    public DVal getDVal() {
        return dVal;
    }

    @Override
    protected void codeExp(DecacCompiler compiler) {
        codeExp(compiler,0);
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        compiler.addInstruction(new LOAD(dVal, Register.getR(compiler,n)));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(impl ? "" : "this");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    String prettyPrintNode() {
        return "This (" + impl + ")";
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

}