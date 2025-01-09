package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;

public class AbstractMethodBody extends Tree{

    final ListDeclVar variables;
    final ListInst body;
    public AbstractMethodBody(ListDeclVar ldv, ListInst li ) {
        this.variables = ldv;
        this.body = li;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        //TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        //TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        //TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

}
