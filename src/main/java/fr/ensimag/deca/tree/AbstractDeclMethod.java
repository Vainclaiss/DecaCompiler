package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;

import org.objectweb.asm.ClassWriter;

public abstract class AbstractDeclMethod extends Tree {

    /**
     * Implements non-terminal "decl_method" of [SyntaxeContextuelle] in pass 2
     * @param compiler contains "env_types" attribute
     * @param superClass the super class of the current class
     * @param current the class that contains the method
     * @return
     * @throws ContextualError
     */
    public abstract MethodDefinition verifyDeclMethod(DecacCompiler compiler, AbstractIdentifier superClass, AbstractIdentifier currentClass)
            throws ContextualError;

    protected abstract void verifyDeclMethodBody(DecacCompiler compiler, EnvironmentExp envExp,
        AbstractIdentifier currentClass) throws ContextualError;

    public abstract AbstractIdentifier getName();

    protected abstract void codeGenDeclMethod(DecacCompiler compiler, ClassDefinition currentClass) throws ContextualError;
    protected abstract void codeGenByteDeclMethod(ClassWriter cw, DecacCompiler compiler, ClassDefinition currentClass) throws ContextualError;

}