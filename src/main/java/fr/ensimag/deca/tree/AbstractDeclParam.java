package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;

public abstract class AbstractDeclParam extends Tree{

    final AbstractIdentifier type;
    final AbstractIdentifier name;
    public AbstractDeclParam(AbstractIdentifier type,AbstractIdentifier name ) {
        this.name = name;
        this.type = type;
    }


    public void decompileDeclParam(IndentPrintStream s) {
        decompile(s);
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        //TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        //TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

    public void verifyDeclParam(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType){
        //TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

    public void codeGenDeclParam(DecacCompiler compiler){
        //TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

}