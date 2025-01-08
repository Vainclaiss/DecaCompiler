package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;

public abstract class AbstractDeclMethod extends Tree {

    final Identifier type;
    final Identifier name;
    final ListDeclParam params;
    final MethodBody body;

    public AbstractDeclMethod(Identifier type, Identifier name, ListDeclParam params, MethodBody body) {
        this.name = name;
        this.type = type;
        this.params = params;
        this.body = body;
    }

    public void decompileDeclMethod(IndentPrintStream s) {
        decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

    public void verifyDeclMethod(DecacCompiler compiler, ClassDefinition superClass) throws ContextualError {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public void codeGenDeclMethod(DecacCompiler compiler) {
        // TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

}