package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.lang.Validate;

/**
 * Declaration of a class (<code>class name extends superClass {members}<code>).
 * 
 * @author gl01
 * @date 01/01/2025
 */
public class DeclClass extends AbstractDeclClass {

    final private AbstractIdentifier name;
    final private AbstractIdentifier superClass;
    final private ListDeclField declFields;
    final private ListDeclMethod declMethods;

    public DeclClass(AbstractIdentifier name, AbstractIdentifier superClass, ListDeclField declFields,
            ListDeclMethod declMethods) {
        Validate.notNull(name);
        Validate.notNull(superClass);
        Validate.notNull(declFields);
        Validate.notNull(declMethods);
        this.name = name;
        this.superClass = superClass;
        this.declFields = declFields;
        this.declMethods = declMethods;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        throw new UnsupportedOperationException("not yet implemented");

    }

    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {

        TypeDefinition superDef = compiler.environmentType.defOfType(superClass.getName());
        if (superDef == null) {
            throw new ContextualError("Error: The parent is not defined", getLocation());
        }

        if (!superDef.isClass()) {
            throw new ContextualError("Error: The parent is not a class", getLocation());
        }
        superClass.setDefinition(superDef);

        // Multiple declarations of the class
        TypeDefinition previousDef = compiler.environmentType.defOfType(name.getName());
        if (previousDef != null) {
            throw new ContextualError("Error: Multiple declaration of " + name.getName().toString()
                    + ", first declaration at " + previousDef.getLocation(), name.getLocation());
        }

        ClassType newType = new ClassType(name.getName(), getLocation(), (ClassDefinition) superDef); // the cast
                                                                                                      // succeed because
                                                                                                      // of the
                                                                                                      // precedent check
        ClassDefinition newDef = newType.getDefinition();
        name.setDefinition(newDef);

        compiler.environmentType.addType(name.getName(), newDef);

    }

    @Override
    protected void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError {

        ClassDefinition superDef = (ClassDefinition) compiler.environmentType.defOfType(superClass.getName());
        // superDef != null et c'est une class d'après la passe 1
        if (!superDef.equals(superClass.getClassDefinition())) {
            throw new ContextualError("Error: Super definitions missmatch", getLocation());
        }

        EnvironmentExp envFields = declFields.verifyListDeclField(compiler, superClass, name);
        EnvironmentExp envMethods = declMethods.verifyListDeclMethod(compiler, superClass, name);

        ClassDefinition nameDef = name.getClassDefinition();
        nameDef.setNumberOfFields(superDef.getNumberOfFields());
        nameDef.setNumberOfMethods(superDef.getNumberOfMethods());

        EnvironmentExp envName = nameDef.getMembers();

        for (Map.Entry<Symbol, ExpDefinition> entry : envFields.getCurrEnv().entrySet()) {
            Symbol symbol = entry.getKey();
            ExpDefinition definition = entry.getValue();
            try {
                envName.declare(symbol, definition);
            } catch (DoubleDefException e) {
                // normalement imposible d'en arriver là car exception l
                throw new ContextualError("Error: A field with the same name is already declared", definition.getLocation());
            }
        }

        // TODO: duplication de code à simplifier
        for (Map.Entry<Symbol, ExpDefinition> entry : envMethods.getCurrEnv().entrySet()) {
            Symbol symbol = entry.getKey();
            ExpDefinition definition = entry.getValue();
            try {
                envName.declare(symbol, definition);
            } catch (DoubleDefException e) {
                throw new ContextualError("Error: A field with the same name is already declared at " 
                                        + envFields.get(symbol).getLocation().toString(), definition.getLocation());
            }
        }
    }

    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        ClassDefinition classDef = (ClassDefinition) compiler.environmentType.defOfType(name.getName());
        if (!classDef.equals(name.getClassDefinition())) {
            throw new ContextualError("Error: Class definitions missmatch", getLocation());
        }

        EnvironmentExp envExp = classDef.getMembers();
        // Passe 3
        declFields.verifyListDeclFieldBody(compiler, envExp, name);
        declMethods.verifyListDeclMethodBody(compiler, envExp, name);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        name.prettyPrint(s,prefix,false);
        superClass.prettyPrint(s,prefix,false);
        declFields.prettyPrint(s,prefix,false);
        declMethods.prettyPrint(s,prefix,false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        throw new UnsupportedOperationException("Not yet supported");
    }

}
