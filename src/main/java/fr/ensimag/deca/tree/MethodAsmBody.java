package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.InlinePortion;
import fr.ensimag.ima.pseudocode.Label;

public class MethodAsmBody extends AbstractMethodBody {
    final StringLiteral code;

    public MethodAsmBody(StringLiteral code) {
        this.code = code;
    }

    @Override
    public void verifyMethodBody(DecacCompiler compiler, EnvironmentExp envExpParams,
            ClassDefinition currentClass, Type returnType) throws ContextualError {

        code.setType(compiler.environmentType.STRING);
    }

    @Override
    public void codeGenMethodBody(DecacCompiler compiler, ClassDefinition curretClass, Label fiLabel) {
        compiler.add(new InlinePortion(code.getValue().replace("\\n", "\n").replace("\\r", "\r").replace("\\t", "\t")));
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        code.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        code.iter(f);
    }

    public void decompile(IndentPrintStream s) {
        s.print("asm(\"" + code.decompile().substring(1, code.decompile().length() - 1).replace("\"", "\\\"") + "\");");
    }

    @Override
    public void codeGenByteMethodBody(MethodVisitor mv, DecacCompiler compiler, Type returnType) {
        String asmCode = code.getValue()
                             .replace("\\n", "\n")
                             .replace("\\r", "\r")
                             .replace("\\t", "\t");
    
        String[] asmInstructions = asmCode.split("\n");
    
        for (String instr : asmInstructions) {
            instr = instr.trim();
            
            if (instr.startsWith("WSTR")) {
                String message = instr.substring(5).trim();
                if (message.startsWith("\"") && message.endsWith("\"")) {
                    message = message.substring(1, message.length() - 1);
                }
    
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitLdcInsn(message);  
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "print", "(Ljava/lang/String;)V", false);
            } 
            else if (instr.startsWith("WINT")) {
                String number = instr.substring(5).trim();
                int intValue;
                try {
                    intValue = Integer.parseInt(number);
                } catch (NumberFormatException e) {
                    throw new UnsupportedOperationException("Invalid integer value in WINT: " + number);
                }
    
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitLdcInsn(intValue);  
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "print", "(I)V", false);
            } 
            else {
                throw new UnsupportedOperationException("Unsupported ASM instruction: " + instr);
            }
        }
    
        if (returnType.isVoid()) {
            mv.visitInsn(Opcodes.RETURN);
        } else {
            throw new UnsupportedOperationException("ASM methods with non-void return type are not supported.");
        }
    }
    
}