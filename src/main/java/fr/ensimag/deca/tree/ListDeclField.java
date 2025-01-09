package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;

import java.lang.reflect.Field;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;

public class ListDeclField extends TreeList<AbstractDeclField> {

    
    public EnvironmentExp verifyListDeclField(DecacCompiler compiler, AbstractIdentifier superClass, AbstractIdentifier currentClass)
    throws ContextualError {
        
        EnvironmentExp envExp = new EnvironmentExp(null);

        for (AbstractDeclField f : getList()) {
            FieldDefinition newField =  f.verifyDeclField(compiler, superClass, currentClass);
            Symbol name = f.getName();
            try {
                envExp.declare(name, newField);
            }
            catch (EnvironmentExp.DoubleDefException e) {
                throw new ContextualError("Error: Multiple declaration of field " + name.toString()
                        + ", first declaration at " + envExp.get(name).getLocation(), f.getLocation());
            }
        }
        
        return envExp;
    }

    public void verifyListDeclFieldBody() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'verifyListDeclFieldBody'");
    }
    
    public void codeGenListDeclField(DecacCompiler compiler, ClassDefinition currentClass) {
        // TODO
    }

    @Override
    public void decompile(IndentPrintStream s) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
}