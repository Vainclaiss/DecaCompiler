package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

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
            throw new ContextualError("Error : The parent is not defined", getLocation());
        }

        if (!superDef.isClass()) {
            throw new ContextualError("Error : The parent is not a class", getLocation());
        }

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
        
        TypeDefinition superDef = compiler.environmentType.defOfType(superClass.getName());
        // superDef != null et c'est une class d'après la passe 1
        superClass.setDefinition(superDef);

        //TODO : à terminer
        //declFields.verifyListDeclField(compiler, superClass.getName(), name.getName());
        //declMethods.verifyListDeclMethod(compiler, name.getClassDefinition().getSuperClass());
    }

    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        // Jamais appellée pour HelloWorld, mais vérifications a faire pour plus tard,
        // avec les tests qui déclenchent les erreurs contextuelles.
        throw new UnsupportedOperationException("not yet implemented");
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
