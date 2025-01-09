package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;




public class DeclMethod extends AbstractDeclMethod {

    final private AbstractIdentifier type;
    final private AbstractIdentifier name;
    final private ListDeclParam params;
    final private MethodBody body;

    public DeclMethod(AbstractIdentifier type, AbstractIdentifier name, ListDeclParam params, MethodBody body) {
        Validate.notNull(type);
        Validate.notNull(name);
        Validate.notNull(params);
        Validate.notNull(body);
        this.type = type;
        this.name = name;
        this.params = params;
        this.body = body;
    }
    
    @Override
    public MethodDefinition verifyDeclMethod(DecacCompiler compiler, AbstractIdentifier superClass, AbstractIdentifier currentClass)
    throws ContextualError {

        Type methodType = type.verifyType(compiler);
        Signature sig = params.verifyListDeclParam(compiler);

        ClassDefinition superDef = (ClassDefinition) compiler.environmentType.defOfType(superClass.getName());
        // superDef != null et c'est une class d'après la passe 1
        superClass.setDefinition(superDef);

        EnvironmentExp envExpSuper = superDef.getMembers();
        ExpDefinition superMethodDef = envExpSuper.get(name.getName());
        
        if (superMethodDef != null) {
            if (!sig.equals(superMethodDef.asMethodDefinition("Error: Cast fail from ExpDefinition to MethodDefinition", getLocation()).getSignature())) {
                throw new ContextualError("Error: redefinition of a method with a different signature", getLocation());
            }
            
            Type superMethodType = superMethodDef.getType();
            if (!methodType.subType(superMethodType)) {
                throw new ContextualError("Error: redefinition of a method with incompatible type", getLocation());
            }
        }

        ClassDefinition currentClassDef = currentClass.getClassDefinition();
        int index;
        if (superMethodDef == null) {
            currentClassDef.incNumberOfMethods();
            index = currentClassDef.getNumberOfMethods();
        }
        else {
            index = superMethodDef.asMethodDefinition("Error: Cast failed from ExpDefinition to MethodDefinition", getLocation()).getIndex();
        }
        MethodDefinition newMethodDefinition = new MethodDefinition(methodType, getLocation(), sig, index);
        name.setDefinition(newMethodDefinition);

        return newMethodDefinition;

    }

    @Override
    protected void verifyDeclMethodBody(DecacCompiler compiler, EnvironmentExp envExp, AbstractIdentifier currentClass)
            throws ContextualError {
        
        Type returnType = type.verifyType(compiler);
        EnvironmentExp envExpParams = params.verifyListDeclParamBody(compiler);
    }
    
    @Override
    public Symbol getName() {
        return name.getName();
    }
    
    public void codeGenDeclMethod(DecacCompiler compiler) {
        // TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

    @Override
    public void decompile(IndentPrintStream s) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'decompile'");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s,prefix,false);
        name.prettyPrint(s,prefix,false);
        params.prettyPrint(s,prefix,false);
        body.prettyPrint(s,prefix,false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

}