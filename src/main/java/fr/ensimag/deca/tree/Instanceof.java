package fr.ensimag.deca.tree;

import static org.mockito.ArgumentMatchers.contains;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

public class Instanceof extends AbstractExpr{

    final private AbstractExpr expr;
    final private AbstractIdentifier compType;

    private ImmediateInteger dVal;
    private boolean value;

    public Instanceof(AbstractExpr leftOperand, AbstractIdentifier rightOperand) {
        Validate.notNull(leftOperand);
        Validate.notNull(rightOperand);
        this.expr = leftOperand;
        this.compType = rightOperand;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        Type type1 = expr.verifyExpr(compiler, localEnv, currentClass);
        Type type2 = compType.verifyType(compiler);

        if (!(type1.isClassOrNull() && type2.isClass())) {
            throw new ContextualError("Error: Incompatible types for operation: " + type1 + " instanceof " + type2, getLocation());
        }

        if (type1.subType(type2)) {
            this.dVal = new ImmediateInteger(1);
            this.value = true;
        }
        else {
            this.dVal = new ImmediateInteger(0);
            this.value = false;
        }

        setType(compiler.environmentType.BOOLEAN);
        return compiler.environmentType.BOOLEAN;
    }

    @Override
    protected DVal getDVal() {
        return dVal;
    }

    @Override
    protected void codeExp(DecacCompiler compiler,int n) {
        expr.codeExp(compiler, 2); // calcul superflu, sert juste à déclencher les erreurs d'exécution
        compiler.addInstruction(new LOAD(dVal, Register.getR(compiler,n)));
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e) {
        if ((value && branchIfTrue) || (!value && !branchIfTrue)) {
            compiler.addInstruction(new BRA(e));
        }
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        s.print(expr.decompile());
        s.print(" instanceof ");
        s.print(compType.decompile());
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'prettyPrintChildren'");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'iterChildren'");
    }

}
