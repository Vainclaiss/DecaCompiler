package fr.ensimag.deca.tree;

import org.objectweb.asm.MethodVisitor;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;

/**
 * List of declarations (e.g. int x; float y,z).
 * 
 * @author gl01
 * @date 01/01/2025
 */
public class ListDeclVar extends TreeList<AbstractDeclVar> {

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclVar declVar : getList()) {
            declVar.decompile(s);
            s.println();
        }
    }

    /**
     * Implements non-terminal "list_decl_var" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains the "env_types" attribute
     * @param localEnv 
     *   its "parentEnvironment" corresponds to "env_exp_sup" attribute
     *   in precondition, its "current" dictionary corresponds to 
     *      the "env_exp" attribute
     *   in postcondition, its "current" dictionary corresponds to 
     *      the "env_exp_r" attribute
     * @param currentClass 
     *          corresponds to "class" attribute (null in the main bloc).
     */    
    void verifyListDeclVariable(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
            for (AbstractDeclVar declVar : getList()) {
                declVar.verifyDeclVar(compiler, localEnv, currentClass);
            }
    }

    protected void codeGenListDeclVar(DecacCompiler compiler, ClassDefinition currentClass) {
        Register baseRegister = (currentClass==null) ? Register.GB : Register.LB;
        
        int offset = 1;
        for (AbstractDeclVar declVar : getList()) {
            declVar.codeGenDeclVar(compiler, new RegisterOffset((currentClass == null) ? compiler.incrGBOffset() : offset++, baseRegister));
        }
        
        compiler.getStackOverflowCounter().addVariables(getList().size());
    }

    protected void codeGenListDeclVarByte(MethodVisitor mv,DecacCompiler compiler)  {
        for (AbstractDeclVar declVar : getList()) {
            declVar.codeGenByteDeclVar(mv,compiler); 
            
        }

    }

}
