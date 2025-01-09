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
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.RFLOAT;
import fr.ensimag.ima.pseudocode.instructions.RINT;

import java.io.PrintStream;

public class MethodCall extends AbstractExpr{
    final private AbstractIdentifier methodName;
    final private AbstractExpr leftOperand;
    final private ListExpr rightOperand;
    public MethodCall(AbstractExpr leftOperand,AbstractIdentifier methodName,ListExpr rightOperand) {
        this.methodName = methodName;
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

       //TODO je sais pas si faut faire ça ou pas
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void codeExp(DecacCompiler compiler) {
        //TODO je sais pas si faut faire ça ou pas
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        //TODO je sais pas si faut faire ça ou pas
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("MethodCall()");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        leftOperand.prettyPrint(s, prefix,false);
        methodName.prettyPrint(s, prefix,false);
        rightOperand.prettyPrint(s, prefix,false);
    }

}
