package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;

public abstract class AbstractDeclField extends Tree {

    final Identifier type;
    final Identifier name;
    final Initialization init;

    public AbstractDeclField(Identifier type, Identifier name, Initialization init) {
        this.name = name;
        this.type = type;
        this.init = init;
    }

    public void decompileDeclField(IndentPrintStream s) {
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

    public void verifyDeclField(DecacCompiler compiler, ClassDefinition currentClass, ClassDefinition superClass)
            throws ContextualError {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public void codeGenDeclField(DecacCompiler compiler) {
        // TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

}