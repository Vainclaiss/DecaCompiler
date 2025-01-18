package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * Instruction
 *
 * @author gl01
 * @date 01/01/2025
 */
public abstract class AbstractInst extends Tree {

    protected final Logger LOG = Logger.getLogger(AbstractInst.class);

    /**
     * Implements non-terminal "inst" of [SyntaxeContextuelle] in pass 3
     * 
     * @param compiler     contains the "env_types" attribute
     * @param localEnv     corresponds to the "env_exp" attribute
     * @param currentClass
     *                     corresponds to the "class" attribute (null in the main
     *                     bloc).
     * @param returnType
     *                     corresponds to the "return" attribute (void in the main
     *                     bloc).
     */
    protected abstract void verifyInst(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, Type returnType) throws ContextualError;

    /**
     * Generate assembly code for the instruction.
     * 
     * @param compiler
     */
    protected abstract void codeGenInst(DecacCompiler compiler);

    protected abstract void codeGenByteInst(MethodVisitor mv,DecacCompiler compiler) throws ContextualError;

    
    /**
     * Generate assembly code for the instruction.
     * 
     * @param compiler
     */
    protected void codeGenInst(DecacCompiler compiler, Label Label) {
        codeGenInst(compiler);
    }

    public boolean isIfThenElse() {
        return false;
    }

    /**
     * Decompile the tree, considering it as an instruction.
     *
     * In most case, this simply calls decompile(), but it may add a semicolon if
     * needed
     */
    protected void decompileInst(IndentPrintStream s) {
        decompile(s);
    }
}
