package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;



import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.execerrors.StackOverflowExecError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.HALT;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.SEQ;
import fr.ensimag.ima.pseudocode.instructions.TSTO;

/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl01
 * @date 01/01/2025
 */
public class Program extends AbstractProgram {
    private static final Logger LOG = Logger.getLogger(Program.class);

    private ListDeclClass classes;
    private AbstractMain main;

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

    public void codeGenByteProgram(DecacCompiler compiler) {

        
        try (FileOutputStream textFileOut = new FileOutputStream("MainBytecode.txt");
             PrintWriter textPrintWriter = new PrintWriter(textFileOut, true))
        {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    
            //Pour afficher le bytecode en forme readable
            TraceClassVisitor tcv = new TraceClassVisitor(cw, textPrintWriter);
    
            // 1) Define the class header
            tcv.visit(
                Opcodes.V17,                // Java 17 class file version
                Opcodes.ACC_PUBLIC,         // public class
                "Main",                     // internal class name
                null,                       // signature (no generics)
                "java/lang/Object",         // superclass: Object
                null                        // no interfaces
            );
    
            // 2) Constructor: public Main() { super(); }

            // on declare le constructeur et on retourne un method visitor
            MethodVisitor ctor = tcv.visitMethod(
                Opcodes.ACC_PUBLIC,
                "<init>",
                "()V",
                null,
                null
            );
            ctor.visitCode(); // on entre dans le corps du constructeur c comme ouvrir {}
            ctor.visitVarInsn(Opcodes.ALOAD, 0);
            ctor.visitMethodInsn( // fait appel à super() dans java
                Opcodes.INVOKESPECIAL, // le invokespecial ici pour dire qu'on va appeler le constructeur
                "java/lang/Object",
                "<init>", // pour dire constructeur
                "()V",// prend rien, retourne rien
                false
            );
            ctor.visitInsn(Opcodes.RETURN); // visitInsn car on insere une instruction sans argument ici return
            ctor.visitMaxs(1, 1);
            ctor.visitEnd();
    
            // 3) Main method
            MethodVisitor mv = tcv.visitMethod(
                Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,
                "main",
                "([Ljava/lang/String;)V",
                null,
                null
            );
            mv.visitCode();
    
            // on entre dans la main method

            this.getMain().codeGenByteMain(mv,compiler); // on appelle cette fct pour generer le bytecode suivant l'arbre
            this.getClasses().codeGenByteClass(compiler);
    
            // 5) On retourne du main
            mv.visitInsn(Opcodes.RETURN); // on ajoute le return pour le compter dans le stack en bas
            mv.visitMaxs(0, 0); // vu qu'on ne sera pas sûr de la profondeur de la pile on rajoute 0,0
            mv.visitEnd();
    
            // 6) La classe est finit
            tcv.visitEnd();
    
            
            byte[] bytecode = cw.toByteArray();

    
            try (FileOutputStream fos = new FileOutputStream("Main.class")) {
                fos.write(bytecode);
                LOG.info("Wrote .class successfully.");
            }
           
            try (PrintWriter rawByteWriter = new PrintWriter("MainRawBytes.txt")) {
                for (byte b : bytecode) {
                    rawByteWriter.printf("%02X ", b);
                }
                rawByteWriter.println();
                LOG.info("Wrote MainRawBytes.txt with a hex dump of the .class file");
            }
    
        } catch (IOException e) {
            throw new RuntimeException("Error writing bytecode output", e);
        }
    }
    

    @Override
    public void codeGenProgram(DecacCompiler compiler) {
        // TODO: compléter ce squelette très rudimentaire de code

        // generation de la table des methodes
        classes.codeGenVtable(compiler);
        compiler.getStackOverflowCounter().addVariables(1); //

        // generation du programme principal
        compiler.addComment("Main program");
        main.codeGenMain(compiler);
        compiler.addInstruction(new HALT());
        compiler.addComment("end main program");

        compiler.addFirst(new ADDSP(compiler.getGBOffset()));
        compiler.addFirst(new BOV(StackOverflowExecError.INSTANCE.getLabel())); // ordre des 2 instructions inversé à
                                                                                // cause de addFirst()
        compiler.addFirst(new TSTO(compiler.getStackOverflowCounter().getMaxTSTO()),
                compiler.getStackOverflowCounter().getDetailsMaxTSTO());

        // code de la methode equals de Object
        compiler.addComment("Code de la methode equals dans la classe Object");
        compiler.addLabel(compiler.environmentType.OBJECT.getDefinition().getVtable()[0]);
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R0));
        compiler.addInstruction(new LOAD(new RegisterOffset(-3, Register.LB), Register.R1));
        compiler.addInstruction(new CMP(Register.R0, Register.R1));
        compiler.addInstruction(new SEQ(Register.R0));
        compiler.addInstruction(new RTS());

        // generation des init et methodes des classes
        compiler.addComment("Initialisation de Object");
        compiler.addLabel(new Label("init.Object"));
        compiler.addInstruction(new RTS());
        
        classes.codeGenClass(compiler);
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
