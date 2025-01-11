package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import org.objectweb.asm.MethodVisitor;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Print statement (print, println, ...).
 *
 * @author gl01
 * @date 01/01/2025
 */
public abstract class AbstractPrint extends AbstractInst {

    private boolean printHex;
    private ListExpr arguments = new ListExpr();

    abstract String getSuffix();

    public AbstractPrint(boolean printHex, ListExpr arguments) {
        Validate.notNull(arguments);
        this.arguments = arguments;
        this.printHex = printHex;
    }

    public ListExpr getArguments() {
        return arguments;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {

        for (AbstractExpr a : getArguments().getList()) {
            Type returnExprType = a.verifyExpr(compiler, localEnv, currentClass);
            if (!(returnExprType.isInt() || returnExprType.isFloat() || returnExprType.isString())) {
                throw new ContextualError(
                        "Error: Expected expression type is int, float or string, got " + returnExprType,
                        a.getLocation());
            }
        }
    } // TODO : faire des tests qui levent cette erreur

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        if (printHex) {
            for (AbstractExpr a : getArguments().getList()) {
                a.codeGenPrintHex(compiler);
            }
        }
        else {
            for (AbstractExpr a : getArguments().getList()) {
                a.codeGenPrint(compiler);
            }
        }
    }
    @Override
    protected void codeGenByteInst(MethodVisitor mv) {
    for (AbstractExpr arg : getArguments().getList()) {

        arg.codeGenBytePrint(mv); // chaque argument appel codegenbytecode
    }
}


    private boolean getPrintHex() {
        return printHex;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        arguments.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        arguments.prettyPrint(s, prefix, true);
    }

}
