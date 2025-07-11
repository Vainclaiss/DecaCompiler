package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;

import java.lang.reflect.Field;
import java.io.PrintStream;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.RTS;

public class ListDeclField extends TreeList<AbstractDeclField> {

    public EnvironmentExp verifyListDeclField(DecacCompiler compiler, AbstractIdentifier superClass,
            AbstractIdentifier currentClass)
            throws ContextualError {

        EnvironmentExp envExp = new EnvironmentExp(null);

        for (AbstractDeclField f : getList()) {
            FieldDefinition newField = f.verifyDeclField(compiler, superClass, currentClass);
            Symbol name = f.getName();
            try {
                envExp.declare(name, newField);
            } catch (EnvironmentExp.DoubleDefException e) {
                throw new ContextualError("Error: Multiple declaration of field '" + name.toString()
                        + "' , first declaration at " + envExp.get(name).getLocation(), f.getLocation());
            }
        }

        return envExp;
    }

    public void verifyListDeclFieldBody(DecacCompiler compiler, EnvironmentExp envExp, AbstractIdentifier currentClass)
            throws ContextualError {
        for (AbstractDeclField f : getList()) {
            f.verifyDeclFieldBody(compiler, envExp, currentClass);
        }
    }

    public void codeGenFieldsInit(DecacCompiler compiler) {
        for (AbstractDeclField f : getList()) {
            f.codeGenFieldInit(compiler);
        }
        compiler.addInstruction(new RTS());
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclField f : getList()) {
            f.decompile(s);
            s.println();
        }
    }

    public void codeGenByteFields(ClassWriter cw, DecacCompiler compiler, String classInternalName) {
        for (AbstractDeclField f : getList()) {
            ((DeclField) f).codeGenByteField(cw, compiler, classInternalName);
        }
    }

    public void codeGenByteFieldsInit(MethodVisitor mv, DecacCompiler compiler, String classInternalName) {
        for (AbstractDeclField f : getList()) {
            ((DeclField) f).codeGenByteFieldInit(mv, compiler, classInternalName);
        }
    }

}