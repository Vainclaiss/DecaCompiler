package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;




public class DeclMethod extends AbstractDeclMethod {

    final private Identifier type;
    final private Identifier name;
    final private ListDeclParam params;
    final private MethodBody body;

    public DeclMethod(Identifier type, Identifier name, ListDeclParam params, MethodBody body) {
        Validate.notNull(type);
        Validate.notNull(name);
        Validate.notNull(params);
        Validate.notNull(body);
        this.type = type;
        this.name = name;
        this.params = params;
        this.body = body;
    }
    
    @Override
    public MethodDefinition verifyDeclMethod(DecacCompiler compiler, AbstractIdentifier superClass)
    throws ContextualError {

        Type methodType = type.verifyType(compiler);
        
        params.verifyListDeclParam(compiler, null, null, methodType);
    }
    
    @Override
    public Symbol getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }
    
    public void codeGenDeclMethod(DecacCompiler compiler) {
        // TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

    @Override
    public void decompile(IndentPrintStream s) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'decompile'");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

}