package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
/**
 * @author gl01
 * @date 01/01/2025
 */
public class Println extends AbstractPrint {

    /**
     * @param arguments arguments passed to the print(...) statement.
     * @param printHex if true, then float should be displayed as hexadecimal (printlnx)
     */
    public Println(boolean printHex, ListExpr arguments) {
        super(printHex, arguments);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        super.codeGenInst(compiler);
        compiler.addInstruction(new WNL());
    }



    @Override
    protected void codeGenBytecode(MethodVisitor mv) {
        super.codeGenBytecode(mv);

        mv.visitFieldInsn(
            Opcodes.GETSTATIC, // la fonction est statique
            "java/lang/System", // la classe system
            "out", // l'operand out
            "Ljava/io/PrintStream;" // la classe printstream?
        );
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL, // la fonction est une instanciation
            "java/io/PrintStream", // la classe printstream indiqué en haut
            "println", // la methode println dans PrintStream
            "()V", // la methode ne prend pas d'argument et ne retourne rien.
            false
        );

        // a la fin le stack va pull printstream et execute println sur les arguments
    }



    @Override
    String getSuffix() {
        return "ln";
    }


}
