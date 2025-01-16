package fr.ensimag.deca.tree;

import java.io.PrintStream;
import java.util.Map;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.TSTOCounter;
import fr.ensimag.deca.codegen.execerrors.StackOverflowExecError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;
import fr.ensimag.ima.pseudocode.instructions.TSTO;

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

    public AbstractIdentifier getNameId() {
        return name;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.println("class " + name.decompile() + " extends " + superClass.decompile() + " {");
        s.indent();
        declFields.decompile(s);
        declMethods.decompile(s);
        s.unindent();
        s.println("}");

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
            throw new ContextualError("Error: Multiple declaration of '" + name.getName().toString()
                    + "' , first declaration at " + previousDef.getLocation(), name.getLocation());
        }

        ClassType newType = new ClassType(name.getName(), getLocation(), superClass.getClassDefinition());  // the cast
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

        ClassDefinition nameDef = name.getClassDefinition();
        nameDef.setNumberOfFields(superDef.getNumberOfFields());
        nameDef.setNumberOfMethods(superDef.getNumberOfMethods());
        
        EnvironmentExp envFields = declFields.verifyListDeclField(compiler, superClass, name);
        EnvironmentExp envMethods = declMethods.verifyListDeclMethod(compiler, superClass, name);

        EnvironmentExp envName = nameDef.getMembers();

        for (Map.Entry<Symbol, ExpDefinition> entry : envFields.getCurrEnv().entrySet()) {
            Symbol symbol = entry.getKey();
            ExpDefinition definition = entry.getValue();
            try {
                envName.declare(symbol, definition);
            } catch (DoubleDefException e) {
                // normalement imposible d'en arriver là car exception l
                throw new ContextualError("Error: A field with the same name is already declared",
                        definition.getLocation());
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
    protected void codeGenVtable(DecacCompiler compiler) {
        ClassDefinition nameDef = name.getClassDefinition();
        nameDef.completeVtable();
        nameDef.codeGenVtable(compiler);
    }

    @Override
    protected void codeGenClass(DecacCompiler compiler) {

        compiler.resetTSTO();
        IMAProgram mainProgram = compiler.getProgram();
        compiler.setProgram(new IMAProgram());

        IMAProgram tempProgram = new IMAProgram();
        tempProgram.addComment("Initialisation des champs de " + name.getName().toString());
        tempProgram.addLabel(new Label("init." + name.getClassDefinition().getType().toString()));

        if ((superClass.getClassDefinition() != null) && !superClass.getClassDefinition().equals(compiler.environmentType.OBJECT.getDefinition())) {
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
            compiler.addInstruction(new PUSH(Register.R1));
            compiler.getStackOverflowCounter().addParamsOnStack(1);
            compiler.addInstruction(new BSR(new Label("init." + superClass.getClassDefinition().getType().toString())));
            compiler.addInstruction(new SUBSP(1));
            compiler.getStackOverflowCounter().addParamsOnStack(-1);
        }

        declFields.codeGenFieldsInit(compiler);

        TSTOCounter stackOverflowCounter = compiler.getStackOverflowCounter();
        int maxSavedRegisters = stackOverflowCounter.getMaxRegisterUsed();
        stackOverflowCounter.addSavedRegisters(maxSavedRegisters-1);

        compiler.addFirst(new Line("fields initialization"));
        compiler.add(new Line("restauration des registres"));
        for (int i = maxSavedRegisters; i >=2 ; i--) {
            compiler.addFirst(new PUSH(Register.getR(compiler,i)));
            compiler.addInstruction(new POP(Register.getR(compiler,i)));
        }

        compiler.addFirst(new Line("sauvegarde des registres"));
        compiler.addFirst(new BOV(StackOverflowExecError.INSTANCE.getLabel())); // ordre des 2 instructions inversé à cause de addFirst()
        compiler.addFirst(new TSTO(compiler.getStackOverflowCounter().getMaxTSTO()), compiler.getStackOverflowCounter().getDetailsMaxTSTO());
        
        tempProgram.append(compiler.getProgram());
        mainProgram.append(tempProgram);
        compiler.setProgram(mainProgram);

        declMethods.codeGenDeclMethods(compiler, name.getClassDefinition());
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        name.prettyPrint(s, prefix, false);
        superClass.prettyPrint(s, prefix, false);
        declFields.prettyPrint(s, prefix, false);
        declMethods.prettyPrint(s, prefix, false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        name.iter(f);
        superClass.iter(f);
        declFields.iter(f);
        declMethods.iter(f);
    }

}
