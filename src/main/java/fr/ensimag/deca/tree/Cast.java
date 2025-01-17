package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.Register;

import java.io.PrintStream;

public class Cast extends AbstractExpr {

    final private AbstractIdentifier type;
    final private AbstractExpr expr;

    public Cast(AbstractIdentifier type, AbstractExpr expr) {
        this.type = type;
        this.expr = expr;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        // Code écrit par Guillaume le 17 janvier a 02:18 soyez indulgent si ça plante
        // ici merci la team
        Type type1 = type.verifyType(compiler);
        Type type2 = expr.verifyExpr(compiler, localEnv, currentClass);

        if (type1.isVoid()) {
            throw new ContextualError("Error: Cannot cast void", getLocation());
        }

        try {
            type.verifyRValue(compiler, localEnv, currentClass, type2);
        } catch (ContextualError typeErr) {
            try {
                expr.verifyRValue(compiler, localEnv, currentClass, type1);
            } catch (ContextualError typeErr2) {
                throw new ContextualError("Error: Cannot cast " + type2.getName() + " to " + type1.getName(),
                        getLocation());
            }
        }

        return type1;
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");

    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        throw new UnsupportedOperationException("not yet implemented");

    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(" + type.getName() + ") (" + expr.decompile() + ")");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        expr.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        expr.prettyPrint(s, prefix, false);
    }
}