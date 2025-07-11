package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DAddr;

import org.objectweb.asm.MethodVisitor;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Initialization (of variable, field, ...)
 *
 * @author gl01
 * @date 01/01/2025
 */
public abstract class AbstractInitialization extends Tree {

    /**
     * Implements non-terminal "initialization" of [SyntaxeContextuelle] in pass 3
     * 
     * @param compiler     contains "env_types" attribute
     * @param t            corresponds to the "type" attribute
     * @param localEnv     corresponds to the "env_exp" attribute
     * @param currentClass
     *                     corresponds to the "class" attribute (null in the main
     *                     bloc).
     */
    protected abstract void verifyInitialization(DecacCompiler compiler,
            Type t, EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;

    protected abstract void codeGenInitialization(DecacCompiler compiler, DAddr adresse);

    protected abstract void codeGenByteInitialization(MethodVisitor mv, int localIndex,DecacCompiler compiler) ;

    public boolean isNoInitialization() {
        return false;
    }
}
