package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;

import org.objectweb.asm.MethodVisitor;
import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;

/**
 * 
 * @author gl01
 * @date 01/01/2025
 */
public class ListInst extends TreeList<AbstractInst> {

    /**
     * Implements non-terminal "list_inst" of [SyntaxeContextuelle] in pass 3
     * 
     * @param compiler     contains "env_types" attribute
     * @param localEnv     corresponds to "env_exp" attribute
     * @param currentClass
     *                     corresponds to "class" attribute (null in the main bloc).
     * @param returnType
     *                     corresponds to "return" attribute (void in the main
     *                     bloc).
     */
    public void verifyListInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        for (AbstractInst i : getList()) {
            Logger.getLogger(Main.class).debug("Verify inst " + i.getClass().getName());
            i.verifyInst(compiler, localEnv, currentClass, returnType);
        }
    }

    public void codeGenListInst(DecacCompiler compiler) {
        for (AbstractInst i : getList()) {
            i.codeGenInst(compiler);
        }
    }

    public void codeGenListInstByte(MethodVisitor mv,DecacCompiler compiler) {
        for (AbstractInst i : getList()) {
            i.codeGenByteInst(mv,compiler); 

        }
    }
    public void codeGenListInst(DecacCompiler compiler, Label finLabel) {
        for (AbstractInst i : getList()) {
            i.codeGenInst(compiler, finLabel);
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractInst i : getList()) {
            i.decompileInst(s);
            s.println();
        }
    }

    public IfThenElse get(int i) {
        throw new UnsupportedOperationException("Unimplemented method 'get'");
    }
}
