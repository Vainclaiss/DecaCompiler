package fr.ensimag.deca.tree;

import org.objectweb.asm.MethodVisitor;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentType;

/**
 * Entry point for contextual verifications and code generation from outside the
 * package.
 * 
 * @author gl01
 * @date 01/01/2025
 *
 */
public abstract class AbstractProgram extends Tree {

    public abstract void verifyProgram(DecacCompiler compiler) throws ContextualError;

    public abstract void codeGenProgram(DecacCompiler compiler);

    public abstract void codeGenByteProgram(DecacCompiler compiler);

}
