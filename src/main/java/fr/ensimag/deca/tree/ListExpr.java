package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;

import java.time.format.SignStyle;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List of expressions (eg list of parameters).
 *
 * @author gl01
 * @date 01/01/2025
 */
public class ListExpr extends TreeList<AbstractExpr> {

    /**
     * Verify the list of parameters of a methodCall
     * 
     * implements non-terminal "rvalue_star" of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler     contains the "env_types" attribute
     * @param localEnv     corresponds to the "env_exp" attribute
     * @param currentClass corresponds to the "class" attribute
     * @param expectedType corresponds to the "type1" attribute
     * @return this with an additional ConvFloat if needed...
     */
    public void verifyRValueStar(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Signature sig) throws ContextualError {

        int n = sig.size();
        int i = 0;
        for (AbstractExpr exp : getList()) {
            if (i == n) {
                throw new ContextualError("Error: Too many arguments in method call", getLocation());
            }
            set(i, exp.verifyRValue(compiler, localEnv, currentClass, sig.paramNumber(i)));
            i++;
        }

        if (i < n) {
            throw new ContextualError("Error: Too few arguments in method call", getLocation());
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        boolean first = true;
        for (AbstractExpr expr : getList()) {
            if (!first) {
                s.print(",");
            }
            expr.decompile(s);
            first = false;
        }
    }
}
