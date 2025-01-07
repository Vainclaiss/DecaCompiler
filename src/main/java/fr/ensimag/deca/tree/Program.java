package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;



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
        // For now, we just call verifyListClassBody + verifyMain
        this.getClasses().verifyListClassBody(compiler);
        this.getMain().verifyMain(compiler);
        LOG.debug("verify program: end");
    }

    public void codeGenByteProgram(DecacCompiler compiler) {
        try (FileOutputStream textFileOut = new FileOutputStream("MainBytecode.txt");
             PrintWriter textPrintWriter = new PrintWriter(textFileOut, true))
        {
          
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

          
            TraceClassVisitor tcv = new TraceClassVisitor(cw, textPrintWriter);

            
            tcv.visit(
                Opcodes.V17,                // Java 17
                Opcodes.ACC_PUBLIC,         // public class
                "Main",                     // internal class name
                null,                       // signature (no generics)
                "java/lang/Object",         // superclass: Object
                null                        // interfaces: none
            );

            // 4) Constructor: public Main() { super(); }
            MethodVisitor ctor = tcv.visitMethod(
                Opcodes.ACC_PUBLIC,
                "<init>",
                "()V",
                null,
                null
            );
            ctor.visitCode();
            ctor.visitVarInsn(Opcodes.ALOAD, 0); // "this"
            ctor.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "java/lang/Object",
                "<init>",
                "()V",
                false
            );
            ctor.visitInsn(Opcodes.RETURN);
            ctor.visitMaxs(1, 1);
            ctor.visitEnd();

            // 5) Main method: public static void main(String[] args)
            MethodVisitor mv = tcv.visitMethod(
                Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,
                "main",
                "([Ljava/lang/String;)V",
                null,
                null
            );
            mv.visitCode();

            // Example instructions: System.out.println("Hello, from ASM!");
            mv.visitFieldInsn(
                Opcodes.GETSTATIC,
                "java/lang/System",
                "out",
                "Ljava/io/PrintStream;"
            );
            mv.visitLdcInsn("Hello");
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(Ljava/lang/String;)V",
                false
            );

           
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0); //Pour le dynamique ASM
            mv.visitEnd();

            // End the class
            tcv.visitEnd();

            // 6) Changer to .byte
            byte[] bytecode = cw.toByteArray();

    
            try (FileOutputStream fos = new FileOutputStream("Main.class")) {
                fos.write(bytecode);
                LOG.info("Successfully wrote Main.class");
            }

            LOG.info("MainBytecode.txt was created containing the textual ASM instructions.");
            LOG.info("Main.class was created - you can run with 'java Main'.");

        } catch (IOException e) {
            throw new RuntimeException("Error writing bytecode output", e);
        }
    }

    @Override
    public void codeGenProgram(DecacCompiler compiler) {

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
