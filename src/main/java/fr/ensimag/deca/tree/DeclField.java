package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;

public class DeclField extends AbstractDeclField {
    final private Visibility visibility;
    final private AbstractIdentifier type;
    final private AbstractIdentifier name;
    final private AbstractInitialization init;

    public DeclField(Visibility visibility, AbstractIdentifier type, AbstractIdentifier name, AbstractInitialization init) {
        Validate.notNull(visibility);
        Validate.notNull(type);
        Validate.notNull(name);
        Validate.notNull(init);
        this.visibility = visibility;
        this.type = type;
        this.name = name;
        this.init = init;
    }

    @Override
    public Symbol getName() {
        return name.getName();
    }

    @Override
    protected FieldDefinition verifyDeclField(DecacCompiler compiler, AbstractIdentifier superClass, AbstractIdentifier currentClass)
            throws ContextualError {
                
        Type nameType = type.verifyType(compiler);
        
        if (nameType.isVoid()) {
            throw new ContextualError("Error: void cannot be used as a type for field declaration ", getLocation());
        }

        ClassDefinition superDef = superClass.getClassDefinition();
        // superDef != null et c'est une class d'après la passe 1
        superClass.setDefinition(superDef);

        ExpDefinition envExpSupeDef = superDef.getMembers().get(name.getName());
        if (envExpSupeDef != null && !envExpSupeDef.isField()) {
            throw new ContextualError("Error: This name is already used for a non field objet at " + envExpSupeDef.getLocation(), getLocation());
        }

        ClassDefinition currentClassDef = currentClass.getClassDefinition();
        int index;
        if (envExpSupeDef == null) {
            currentClassDef.incNumberOfFields();
            index = currentClassDef.getNumberOfFields();
        }
        else {
            index = envExpSupeDef.asFieldDefinition("Error: Cast failed from ExpDefinition to FieldDefinition", getLocation()).getIndex();
        }

        FieldDefinition newFieldDefinition = new FieldDefinition(nameType, getLocation(), visibility, superDef, index);
        name.setDefinition(newFieldDefinition);

        return newFieldDefinition;
    }


    @Override
    protected void verifyDeclFieldBody(DecacCompiler compiler, EnvironmentExp envExp, AbstractIdentifier currentClass)
            throws ContextualError {
        
        Type nameType = type.verifyType(compiler);
        // TODO: void type authorisé ??
        init.verifyInitialization(compiler, nameType, envExp, currentClass.getClassDefinition());
    }


    protected void codeGenDeclField(DecacCompiler compiler) {
        // TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

    @Override
    public void decompile(IndentPrintStream s) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'decompile'");
    }

    @Override
    protected String prettyPrintNode(){return "DeclField (" + visibility +")";}

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s,prefix,false);
        name.prettyPrint(s,prefix,false);
        init.prettyPrint(s,prefix,false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

}