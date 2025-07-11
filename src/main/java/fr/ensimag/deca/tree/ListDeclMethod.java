package fr.ensimag.deca.tree;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;

/**
 * List of method declarations: DeclMethod.
 */
public class ListDeclMethod extends TreeList<AbstractDeclMethod> {

    public EnvironmentExp verifyListDeclMethod(DecacCompiler compiler,
            AbstractIdentifier superClass, AbstractIdentifier currentClass)
            throws ContextualError {

        EnvironmentExp envExp = new EnvironmentExp(null);

        for (AbstractDeclMethod m : getList()) {
            MethodDefinition newMethod = m.verifyDeclMethod(compiler, superClass, currentClass);
            Symbol name = m.getName().getName();
            try {
                envExp.declare(name, newMethod);
            } catch (EnvironmentExp.DoubleDefException e) {
                throw new ContextualError(
                    "Error: Multiple declaration of '" + name + 
                    "' , first declaration at " +
                    envExp.get(name).getLocation(), 
                    m.getLocation()
                );
            }
        }

        return envExp;
    }

    public void verifyListDeclMethodBody(DecacCompiler compiler,
                                         EnvironmentExp envExp,
                                         AbstractIdentifier currentClass)
            throws ContextualError {
        for (AbstractDeclMethod m : getList()) {
            m.verifyDeclMethodBody(compiler, envExp, currentClass);
        }
    }


    public void codeGenDeclMethods(DecacCompiler compiler, ClassDefinition currentClass) {
        for (AbstractDeclMethod m : getList()) {
            m.codeGenDeclMethod(compiler, currentClass);
        }
    }

   
    public void codeGenByteDeclMethods(ClassWriter cw, DecacCompiler compiler, ClassDefinition currentClass) {
     
        for (AbstractDeclMethod m : getList()) {
            m.codeGenByteDeclMethod(cw, compiler, currentClass);
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclMethod m : getList()) {
            m.decompile(s);
        }
    }
}
