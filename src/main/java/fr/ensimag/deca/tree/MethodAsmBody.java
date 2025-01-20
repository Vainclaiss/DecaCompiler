package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.objectweb.asm.MethodVisitor;

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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'codeGenByteMethodBody'");
    }
}