package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;

import java.io.PrintStream;
import java.lang.reflect.Method;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;




public class DeclParam extends AbstractDeclParam {

    final AbstractIdentifier type;
    final AbstractIdentifier name;

    public DeclParam(AbstractIdentifier type,AbstractIdentifier name ) {
        this.name = name;
        this.type = type;
    }

    @Override
    protected AbstractIdentifier getName() {
        return name;
    }


    public void decompileDeclParam(IndentPrintStream s) {
        decompile(s);
    }

    @Override
    public Type verifyDeclParam(DecacCompiler compiler) throws ContextualError {

        Type paramType = type.verifyType(compiler);
        if (paramType.isVoid()) {
            throw new ContextualError("Error: Method parameters cannot have a void type", getLocation());
        }

        return paramType;
    }

    @Override
    protected ParamDefinition verifyDeclParamBody(DecacCompiler compiler) throws ContextualError {
        
        Type paramType = type.verifyType(compiler);
        
        ParamDefinition newParamDef = new ParamDefinition(paramType, getLocation());
        name.setDefinition(newParamDef);

        return newParamDef;
    }

    @Override
    public void codeGenDeclParam(DecacCompiler compiler) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'codeGenDeclParam'");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s,prefix,true);
        name.prettyPrint(s,prefix,false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        throw new UnsupportedOperationException("Unimplemented method 'iterChildren'");
    }
    public void decompile(IndentPrintStream s) {
        s.print(type.decompile() + " " + name.decompile());
    }

}