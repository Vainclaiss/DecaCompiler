package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;

public class ListDeclMethod extends TreeList<AbstractDeclMethod> {

    @Override
    public void decompile(IndentPrintStream s) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void verifyListDeclMethod(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType){
        //TODO
    }

    public void codeGenListDeclMethod(DecacCompiler compiler, ClassDefinition currentClass) {
        //TODO
    }


}