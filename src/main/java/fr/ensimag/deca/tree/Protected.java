package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;

public class Protected extends AbstractVisibility{
    public Protected() {}

    public void verifyVisibility(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, Type returnType) {
        //TODO
    }
    public void codeGenVisibility(DecacCompiler compiler){
        //TODO
    }
    public void decompile(IndentPrintStream s){
        //TODO
    }

    @Override
    public void prettyPrintChildren(PrintStream s, String prefix) {
        //TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

    @Override
    public void iterChildren(TreeFunction f) {
        //TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }
}
