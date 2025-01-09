package fr.ensimag.deca.tree;

import java.io.PrintStream;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;


public class MethodAsmBody extends AbstractMethodBody {
    final StringLiteral string;
    public MethodAsmBody(StringLiteral string) {
        this.string = string;
    }

   public void decompileMethodAsmBody(IndentPrintStream s) {
        decompile(s);
    }

    @Override
    public void verifyMethodBody(DecacCompiler compiler, EnvironmentExp envExpParams,
                ClassDefinition currentClass, Type returnType) throws ContextualError {
         // nothing to do
    }


    @Override
    public void codeGenMethodBody(DecacCompiler compiler) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'codeGenMethodBody'");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        string.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        //TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }
    public void decompile(IndentPrintStream s) {
        //TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }
}