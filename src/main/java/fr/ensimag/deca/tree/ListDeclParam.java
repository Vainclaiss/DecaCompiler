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


/**
 * 
 * @author gl01
 * @date 01/01/2025
 */
public class ListDeclParam extends TreeList<AbstractDeclParam> {

    /**
     * Implements non-terminal "list_DeclParam" of [SyntaxeContextuelle] in pass 3
     * 
     * @param compiler     contains "env_types" attribute
     * @param localEnv     corresponds to "env_exp" attribute
     * @param currentClass
     *                     corresponds to "class" attribute (null in the main bloc).
     * @param returnType
     *                     corresponds to "return" attribute (void in the main
     *                     bloc).
     */
    public Signature verifyListDeclParam(DecacCompiler compiler) throws ContextualError {
        Signature sig = new Signature();
        for (AbstractDeclParam param : getList()) {
            Type paramType = param.verifyDeclParam(compiler);
            sig.add(paramType);
        }

        return sig;
    }

    public EnvironmentExp verifyListDeclParamBody(DecacCompiler compiler) throws ContextualError {
            
        EnvironmentExp envExp = new EnvironmentExp(null);

        for (AbstractDeclParam param : getList()) {
            ParamDefinition newParamDef =  param.verifyDeclParamBody(compiler);
            Symbol name = param.getName();
            try {
                envExp.declare(name, newParamDef);
            }
            catch (EnvironmentExp.DoubleDefException e) {
                throw new ContextualError("Error: Multiple declaration of parameter " + name.toString()
                        + ", first declaration at " + envExp.get(name).getLocation(), param.getLocation());
            }
        }
        
        return envExp;
    }

    public void codeGenListDeclParam(DecacCompiler compiler) {
        //TODO
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclParam i : getList()) {
            i.decompile(s);
            s.println();
        }
    }

}