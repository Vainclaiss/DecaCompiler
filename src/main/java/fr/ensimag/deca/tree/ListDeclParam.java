package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;

import org.objectweb.asm.MethodVisitor;

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
    public void codeGenByteParamsInit(MethodVisitor mv, DecacCompiler compiler) {
        // Index 0 is 'this' for instance methods
        int paramIndex = 1;
    
        for (AbstractDeclParam param : getList()) {
            // We'll call verifyDeclParam again if you haven't stored the type
            // This only works if calling it again won't break or re-check incorrectly
            Type paramType;
            try {
                paramType = param.verifyDeclParam(compiler);
            } catch (ContextualError e) {
                // If verification fails, handle or throw a runtime exception
                throw new RuntimeException(e);
            }
    
            // A debug name for the local variable
            String paramName = param.getName().getName().toString();
            // Convert it to JVM descriptor
            String paramDescriptor = paramType.toJVMDescriptor();
    
            // Optionally define debug info for local variable
            mv.visitLocalVariable(
                paramName,
                paramDescriptor,
                null,
                null,
                null,
                paramIndex
            );
    
            // Move to the next local index
            // If you do not handle 64-bit types, just do paramIndex++
            paramIndex++;
    
            // If you actually need to handle double/long, do something like:
            // paramIndex += paramType.isDoubleOrLong() ? 2 : 1;
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