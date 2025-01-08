package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;

public class ListDeclMethod extends TreeList<AbstractDeclMethod> {

    public EnvironmentExp verifyListDeclMethod(DecacCompiler compiler, AbstractIdentifier superClass) throws ContextualError {
        
        EnvironmentExp envExp = new EnvironmentExp(null);
        int index = 0;
        for (AbstractDeclMethod m : getList()) {
            MethodDefinition newMethod =  m.verifyDeclMethod(compiler, superClass, index);
            Symbol name = m.getName();

            try {
                envExp.declare(name, newMethod);
            }
            catch (EnvironmentExp.DoubleDefException e) {
                throw new ContextualError("Error: Multiple declaration of " + name.toString()
                        + ", first declaration at " + envExp.get(name).getLocation(), m.getLocation());
            }
        }
        
        return envExp;
    }

    public void codeGenListDeclMethod(DecacCompiler compiler, AbstractIdentifier superCLass) {
        // TODO
    }

    @Override
    public void decompile(IndentPrintStream s) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}