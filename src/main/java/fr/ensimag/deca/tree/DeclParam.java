package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;

public class DeclParam extends AbstractDeclParam {

    private final AbstractIdentifier type;
    private final AbstractIdentifier name;

    // Add a field to store the resolved type after verification
    private Type resolvedType;

    public DeclParam(AbstractIdentifier type, AbstractIdentifier name) {
        this.name = name;
        this.type = type;
    }

    @Override
    protected AbstractIdentifier getName() {
        return name;
    }

    @Override
    public Type verifyDeclParam(DecacCompiler compiler) throws ContextualError {
        // This is called in pass 2 to check the param type
        Type paramType = type.verifyType(compiler);
        if (paramType.isVoid()) {
            throw new ContextualError(
                "Error: Method parameters cannot have a void type",
                getLocation()
            );
        }

        // Store the resolved type
        this.resolvedType = paramType;
        return paramType;
    }

    @Override
    protected ParamDefinition verifyDeclParamBody(DecacCompiler compiler) throws ContextualError {
        // Typically done in pass 3. The type should already be known.
        if (resolvedType == null) {
            // fallback if not set or if your compiler calls this first
            resolvedType = type.verifyType(compiler);
        }

        ParamDefinition newParamDef = new ParamDefinition(resolvedType, getLocation());
        name.setDefinition(newParamDef);

        return newParamDef;
    }

    /**
     * Return the type resolved during semantic checks.
     */
    public Type getResolvedType() {
        if (resolvedType == null) {
            throw new IllegalStateException(
                "Param type is not resolved yet. Make sure verifyDeclParam is called first."
            );
        }
        return resolvedType;
    }

    @Override
    public void codeGenDeclParam(DecacCompiler compiler) {
        throw new UnsupportedOperationException("Unimplemented method 'codeGenDeclParam'");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, true);
        name.prettyPrint(s, prefix, false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        name.iter(f);
    }

    public void decompile(IndentPrintStream s) {
        s.print(type.decompile() + " " + name.decompile());
    }

    @Override
    protected Type getType() {
        if (resolvedType == null) {
            throw new IllegalStateException(
                "Parameter type not resolved. Make sure verifyDeclParam was called."
            );
        }
        return resolvedType;
    }
    
}
