package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;

import org.objectweb.asm.MethodVisitor;

public abstract class AbstractDeclField extends Tree {


    /**
     * Implements non-terminal "decl_field" of [SyntaxeContextuelle] in pass 2
     * @param compiler contains "env_types" attribute
     * @param currentClass the class that contains the field
     * @param superClass the super class of the current class
     * @return
     * 
     */
    protected abstract FieldDefinition verifyDeclField(DecacCompiler compiler, AbstractIdentifier superClass,
        AbstractIdentifier currentClass) throws ContextualError;

    protected abstract void verifyDeclFieldBody(DecacCompiler compiler, EnvironmentExp envExp,
        AbstractIdentifier currentClass) throws ContextualError;
        
    public abstract Symbol getName();

    protected abstract void codeGenFieldInit(DecacCompiler compiler);
    protected abstract void codeGenByteFieldInit(MethodVisitor mv, DecacCompiler compiler, String classInternalName) throws ContextualError;

}