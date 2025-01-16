package fr.ensimag.deca.tree;

import java.io.PrintStream;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.execerrors.MissingReturnError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;

public class MethodBody extends AbstractMethodBody {

    final ListDeclVar variables;
    final ListInst insts;

    public MethodBody(ListDeclVar ldv, ListInst li) {
        this.variables = ldv;
        this.insts = li;
    }

    @Override
    public void verifyMethodBody(DecacCompiler compiler, EnvironmentExp envExpParams,
            ClassDefinition currentClass, Type returnType) throws ContextualError {

        variables.verifyListDeclVariable(compiler, envExpParams, currentClass);
        insts.verifyListInst(compiler, envExpParams, currentClass, returnType);
    }

    @Override
    public void codeGenMethodBody(DecacCompiler compiler, ClassDefinition currentClass, Label finLabel) {
        variables.codeGenListDeclVar(compiler, currentClass);
        insts.codeGenListInst(compiler, finLabel);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        variables.prettyPrint(s, prefix, true);
        insts.prettyPrint(s, prefix, false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

    public void decompile(IndentPrintStream s) {
        s.println("{");
        s.indent();
        s.indent(); // TODO: trouver un autre moyen j'aime pas le double indent
        variables.decompile(s);
        insts.decompile(s);
        s.unindent();
        s.println("}");
    }

}
