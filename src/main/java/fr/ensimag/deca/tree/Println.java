package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Println statement.
 * 
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
    protected void codeGenByteInst(MethodVisitor mv) {
        System.out.println("DEBUG: Println.codeGenByteInst is invoked");
    
        // Load System.out
        mv.visitFieldInsn(
            Opcodes.GETSTATIC,
            "java/lang/System",
            "out",
            "Ljava/io/PrintStream;"
        );
    
        
        if (!getArguments().isEmpty()) {
            AbstractExpr firstArg = getArguments().get(0);
            if (firstArg instanceof StringLiteral) {
                StringLiteral str = (StringLiteral) firstArg;
                mv.visitLdcInsn(str.getValue());
            } else {
                throw new UnsupportedOperationException("Unsupported arg type in println");
            }
        } else {
            mv.visitLdcInsn("");
        }
    
        // call println(String)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(Ljava/lang/String;)V",
            false
        );
    }
    

    @Override
    String getSuffix() {
        return "ln";
    }
}
