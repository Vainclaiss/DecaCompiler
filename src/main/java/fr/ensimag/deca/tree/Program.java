package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.*;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


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

    /*
     * Correspond au verifyProgram de la passe 3 (car pas de valeur renvoyée /
     * synthétisée)
     */
    @Override
    public void verifyProgram(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify program: start");
        // Rien a verifier, voir règle (3.1)

        // Directement la passe 3 pour l'instant
        this.getClasses().verifyListClassBody(compiler);
        this.getMain().verifyMain(compiler);
        LOG.debug("verify program: end");
    }



    /**
     * New method: Java bytecode generation using ASM.
     * Produces a "Main.class" file with:
     *   public static void main(String[] args) { ... }
     * inside it, by calling main.codeGenMainBytecode(...).
     */

     // UNE ENTREE NECESSAIRE POUR LA GENERATION BYTECODE
     // ICI on a imperativement besoin du main -- JVM en a besoin.
    public void codeGenBytecode(DecacCompiler compiler) {


        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

       // on definit ici les métadonnées de la classe
        cw.visit(
            Opcodes.V17, // JAVA 17            
            Opcodes.ACC_PUBLIC,    // PUBLIC      
            "Main",       // class main              
            null,                     
            "java/lang/Object",         
            null                       
        );

        MethodVisitor ctor = cw.visitMethod( // on est en train de créer un methodvisitor different pour chaque "methode"
            Opcodes.ACC_PUBLIC, 
            "<init>", // constructeur
            "()V",  // prend rien retoruen rien
            null, 
            null
        );
        ctor.visitCode();
        // this.super();
        ctor.visitVarInsn(Opcodes.ALOAD, 0); // on load la variable local
        ctor.visitMethodInsn(
            Opcodes.INVOKESPECIAL, // on invoke le constructeur
            "java/lang/Object", // classe objet
            "<init>", // le constructeur qu'on appelle
            "()V", 
            false
        );
        ctor.visitInsn(Opcodes.RETURN); // return void
        ctor.visitMaxs(1, 1);
        ctor.visitEnd();

        MethodVisitor mv = cw.visitMethod( 
            Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, // public - static
            "main",// fonction main
            "([Ljava/lang/String;)V", // prend un array de string
            null,
            null
        );
        mv.visitCode(); // on declare qu'on va aller ddans le body de la méthode

        this.main.codeGenMainBytecode(mv); 

        mv.visitInsn(Opcodes.RETURN);

        mv.visitMaxs(0, 0); // ici le maxstack et maxlocal sont calculés dynamiquepent par la librairie
        mv.visitEnd();

        cw.visitEnd();

        byte[] b = cw.toByteArray();
        try (FileOutputStream fos = new FileOutputStream("Main.class")) {
            fos.write(b);
            LOG.info("Main program");
        } catch (IOException e) {
            throw new RuntimeException("Failed to write Main.class", e);
        }
    }


    @Override
    public void codeGenProgram(DecacCompiler compiler) {
        // A FAIRE: compléter ce squelette très rudimentaire de code
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
