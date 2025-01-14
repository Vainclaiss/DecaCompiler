package fr.ensimag.deca.tree;

import java.io.PrintStream;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.HALT;

/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl01
 * @date 01/01/2025
 */
public class Program extends AbstractProgram {
    private static final Logger LOG = Logger.getLogger(Program.class);

    public Program(ListDeclClass classes, AbstractMain main) {
        Validate.notNull(classes);
        Validate.notNull(main);
        this.classes = classes;
        this.main = main;
    }

    public ListDeclClass getClasses() {
        return classes;
    }

    public AbstractMain getMain() {
        return main;
    }

    private ListDeclClass classes;
    private AbstractMain main;

    @Override
    public void verifyProgram(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify program: start");
        // Rien a verifier, voir règle (3.1)

        this.getClasses().verifyListClass(compiler);
        this.getClasses().verifyListClassMembers(compiler);
        this.getClasses().verifyListClassBody(compiler);
        this.getMain().verifyMain(compiler);
        LOG.debug("verify program: end");
    }

    @Override
    public void codeGenProgram(DecacCompiler compiler) {
        // TODO: compléter ce squelette très rudimentaire de code

        // création of the vTable
        for (Map.Entry<Symbol, TypeDefinition> classEntry : compiler.environmentType.getEnvTypes().entrySet()) {
            Symbol classSymbol = classEntry.getKey();
            TypeDefinition typeDef = classEntry.getValue();
            if (typeDef.isClass()) {
                ClassDefinition classDef = (ClassDefinition) typeDef; // cast succeed because of the check
                Label[] vtable = new Label[classDef.getNumberOfMethods()];

                for (Map.Entry<Symbol, ExpDefinition> methodEntry : classDef.getMembers().getCurrEnv().entrySet()) {
                    Symbol methodSymbol = methodEntry.getKey();
                    ExpDefinition expDef = methodEntry.getValue();
                    if (expDef.isMethod()) {
                        MethodDefinition methodDef = (MethodDefinition) expDef;   // the cast succeed because of the check

                        Label methodLabel = new Label("code." + classSymbol.toString() + "." + methodSymbol.toString());

                        methodDef.setLabel(methodLabel);
                        vtable[methodDef.getIndex()] = methodLabel;
                    }
                }

                classDef.setVtable(vtable);
            }
        }
        
        compiler.addComment("Main program");
        main.codeGenMain(compiler);
        compiler.addInstruction(new HALT());
    }

    @Override
    public void decompile(IndentPrintStream s) {
        getClasses().decompile(s);
        getMain().decompile(s);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        classes.iter(f);
        main.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classes.prettyPrint(s, prefix, false);
        main.prettyPrint(s, prefix, true);
    }
}
