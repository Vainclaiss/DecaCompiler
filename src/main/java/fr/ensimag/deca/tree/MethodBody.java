package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;



public class MethodBody extends AbstractMethodBody {
    public MethodBody(ListDeclVar ldv, ListInst li ){
        super(ldv,li);
    }

    public void verifyMethodBody(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType){
        //TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

    public void codeGenMethodBody(DecacCompiler compiler){
        //TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

}
