package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;



public class AsmBody extends AbstractAsmBody {
    public AsmBody(StringLiteral string){
        super(string);
    }

    public void verifyAsmBody(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType){
        //TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

    public void codeGenAsmBody(DecacCompiler compiler){
        //TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

}