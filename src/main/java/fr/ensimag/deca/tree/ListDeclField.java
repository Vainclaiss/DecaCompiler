package fr.ensimag.deca.tree;

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

/**
 * List of field declarations: DeclField.
 */
public class ListDeclField extends TreeList<AbstractDeclField> {

    public EnvironmentExp verifyListDeclField(
            DecacCompiler compiler,
            AbstractIdentifier superClass,
            AbstractIdentifier currentClass
    ) throws ContextualError {
        EnvironmentExp envExp = new EnvironmentExp(null);
        for (AbstractDeclField f : getList()) {
            FieldDefinition newField = f.verifyDeclField(compiler, superClass, currentClass);
            Symbol name = f.getName();
            try {
                envExp.declare(name, newField);
            } catch (DoubleDefException e) {
                throw new ContextualError(
                    "Error: Multiple declaration of field '" + name + 
                    "' , first declaration at " + 
                    envExp.get(name).getLocation(), 
                    f.getLocation()
                );
            }
        }
        return envExp;
    }

    public void verifyListDeclFieldBody(
            DecacCompiler compiler,
            EnvironmentExp envExp,
            AbstractIdentifier currentClass
    ) throws ContextualError {
        for (AbstractDeclField f : getList()) {
            f.verifyDeclFieldBody(compiler, envExp, currentClass);
        }
    }

    // ------------------ IMA Code Generation ------------------ //
    public void codeGenFieldsInit(DecacCompiler compiler) {
        for (AbstractDeclField f : getList()) {
            f.codeGenFieldInit(compiler);
        }
        // Possibly end with RTS if it's a subroutine:
        // compiler.addInstruction(new RTS());
    }

    // ------------------ ASM Bytecode Generation ------------------ //

    /**
     * Declare each field in the `.class` (signature only, no init).
     * Called by DeclClass for field **declarations**.
     */
    public void codeGenByteFields(
            ClassWriter cw,
            DecacCompiler compiler,
            String classInternalName
    ) {
        for (AbstractDeclField f : getList()) {
            // We assume each f is actually a DeclField
            ((DeclField) f).codeGenByteField(cw, compiler, classInternalName);
        }
    }

    /**
     * Initialize each field in the **constructor** or an init method.
     * Called inside the generated constructor if you have explicit initial values.
     */
    public void codeGenByteFieldsInit(
            MethodVisitor mv,
            DecacCompiler compiler,
            String classInternalName
    ) {
        for (AbstractDeclField f : getList()) {
            // We assume each f is actually a DeclField
            ((DeclField) f).codeGenByteFieldInit(mv, compiler, classInternalName);
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclField f : getList()) {
            f.decompile(s);
            s.println();
        }
    }
}
