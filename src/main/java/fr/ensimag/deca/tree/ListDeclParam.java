package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;

/**
 * 
 * @author gl01
 * @date 01/01/2025
 */
public class ListDeclParam extends TreeList<AbstractDeclParam> {

    /**
     * Implements non-terminal "list_DeclParam" of [SyntaxeContextuelle] in pass 3
     * 
     * @param compiler contains "env_types" attribute
     */
    public Signature verifyListDeclParam(DecacCompiler compiler) throws ContextualError {
        Signature sig = new Signature();
        for (AbstractDeclParam param : getList()) {
            Type paramType = param.verifyDeclParam(compiler);
            sig.add(paramType);
        }

        return sig;
    }

    public EnvironmentExp verifyListDeclParamBody(DecacCompiler compiler, EnvironmentExp envExp)
            throws ContextualError {

        EnvironmentExp envExpParam = new EnvironmentExp(envExp);

        for (AbstractDeclParam param : getList()) {
            ParamDefinition newParamDef = param.verifyDeclParamBody(compiler);
            Symbol name = param.getName().getName();
            try {
                envExpParam.declare(name, newParamDef);
            } catch (EnvironmentExp.DoubleDefException e) {
                throw new ContextualError("Error: Multiple declaration of parameter '" + name.toString()
                        + "' , first declaration at " + envExpParam.get(name).getLocation(), param.getLocation());
            }
        }

        return envExpParam;
    }

    public void codeGenListDeclParams(DecacCompiler compiler) {
        int offset = -3;
        for (AbstractDeclParam param : getList()) {
            param.getName().getExpDefinition().setOperand(new RegisterOffset(offset--, Register.LB));
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclParam i : getList()) {
            s.print(i.decompile());
            if (i != getList().get(getList().size() - 1)) {
                s.print(", ");
            }
        }
    }

}