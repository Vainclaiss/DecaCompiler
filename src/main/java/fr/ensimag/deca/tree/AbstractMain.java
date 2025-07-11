package fr.ensimag.deca.tree;

import org.objectweb.asm.MethodVisitor;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;

/**
 * Main block of a Deca program.
 *
 * @author gl01
 * @date 01/01/2025
 */
public abstract class AbstractMain extends Tree {

    protected abstract void codeGenMain(DecacCompiler compiler);
    

    /**
     * Implements non-terminal "main" of [SyntaxeContextuelle] in pass 3
     */
    protected abstract void verifyMain(DecacCompiler compiler) throws ContextualError;

    protected abstract void codeGenByteMain(MethodVisitor mv, DecacCompiler compiler) ;
}
