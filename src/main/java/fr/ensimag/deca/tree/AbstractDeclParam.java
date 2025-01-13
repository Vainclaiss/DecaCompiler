package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;

public abstract class AbstractDeclParam extends Tree{

    public abstract Type verifyDeclParam(DecacCompiler compiler) throws ContextualError;

    public abstract void codeGenDeclParam(DecacCompiler compiler) ;

    protected abstract ParamDefinition verifyDeclParamBody(DecacCompiler compiler) throws ContextualError;

    protected abstract Symbol getName();

}