package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.execerrors.IOError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.RFLOAT;
import fr.ensimag.ima.pseudocode.instructions.RINT;

import java.io.PrintStream;

/**
 *
 * @author gl01
 * @date 01/01/2025
 */
public class New extends AbstractExpr {

    final private AbstractIdentifier name;

    public New(AbstractIdentifier name) {
        this.name = name;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        Type type = name.verifyType(compiler);
        if (!type.isClass()) {
            throw new ContextualError("Error: Cannot create an object from a non class type", getLocation());
        }
        setType(type);
        return type;
    }

    @Override
    protected void codeExp(DecacCompiler compiler) {
        //TODO je sais pas si faut faire ça ou pas
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        //TODO je sais pas si faut faire ça ou pas
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new ");
        name.decompile(s);
        s.print("()");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        name.prettyPrint(s,prefix,false);
    }

}
