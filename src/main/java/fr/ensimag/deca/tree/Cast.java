package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.execerrors.IncompatibleCastError;
import fr.ensimag.deca.codegen.execerrors.OverflowError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.INT;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
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
    protected DVal getDVal() {
        return expr.getDVal();
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        Type type1 = type.verifyType(compiler);
        Type type2 = expr.verifyExpr(compiler, localEnv, currentClass);

        if (type1.isVoid()) {
            throw new ContextualError("Error: Cannot cast void", getLocation());
        }

        try {
            expr.verifyRValue(compiler, localEnv, currentClass, type2);
        } catch (ContextualError typeErr) {
            try {
                expr.verifyRValue(compiler, localEnv, currentClass, type1);
            } catch (ContextualError typeErr2) {
                throw new ContextualError("Error: Cannot cast " + type2.getName() + " to " + type1.getName(),
                        getLocation());
            }
        }
        
        expr.setType(type1);
        setType(type1);
        return type1;
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        compiler.addComment("debut du cast " + expr.getType().toString() + " vers " + type.getName().getName());
        if (type.getType().isInt()) {
            expr.codeExp(compiler, n);
            compiler.addInstruction(new INT(Register.getR(n), Register.getR(n)));
        }
        else if (type.getType().isFloat()) {
            expr.codeExp(compiler, n);
            compiler.addInstruction(new FLOAT(Register.getR(n), Register.getR(n)));
        }
        else if (type.getType().isClass() && !expr.getType().isNull()) {

            if (!compiler.getCompilerOptions().getSkipExecErrors()) {
                compiler.addExecError(IncompatibleCastError.INSTANCE);
                //compiler.addInstruction(new BOV(OverflowError.INSTANCE.getLabel()));
            }

            (new Instanceof(expr, type)).codeGenBool(compiler, false, IncompatibleCastError.INSTANCE.getLabel(), n);
        }
        else {
            expr.codeExp(compiler, n);
        }
        compiler.addComment("fin du cast " + expr.getType().toString() + " vers " + type.getName().getName());
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