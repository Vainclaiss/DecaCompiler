package fr.ensimag.deca.tree;

import java.io.PrintStream;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;




public class MethodBody extends AbstractMethodBody {

    final ListDeclVar variables;
    final ListInst body;
    public MethodBody(ListDeclVar ldv, ListInst li ) {
        this.variables = ldv;
        this.body = li;
    }

   public void decompileMethodBody(IndentPrintStream s) {
        decompile(s);
    }

    @Override
    public void verifyMethodBody(DecacCompiler compiler, EnvironmentExp envExpParams,
                ClassDefinition currentClass, Type returnType) throws ContextualError {

        variables.verifyListDeclVariable(compiler, envExpParams, currentClass);
        body.verifyListInst(compiler, envExpParams, currentClass, returnType);
    }


    @Override
    public void codeGenMethodBody(DecacCompiler compiler) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'codeGenMethodBody'");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        variables.prettyPrint(s,prefix,true);
        body.prettyPrint(s,prefix,false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        //TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }
    public void decompile(IndentPrintStream s) {
        //TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

}
