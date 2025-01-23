package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.StringType;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.instructions.WUTF8;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.PrintStream;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.Validate;

/**
 * String literal
 *
 * @author gl01
 * @date 01/01/2025
 */
public class StringLiteral extends AbstractStringLiteral {

    @Override
    public String getValue() {
        return value;
    }

    private String value;

    public StringLiteral(String value) {
        Validate.notNull(value);
        this.value = value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        setType(compiler.environmentType.STRING);
        return compiler.environmentType.STRING;
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        String regex = "(\\\\n|\\\\r|\\\\t)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);

        int lastEnd = 0;

        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                compiler.addInstruction(new WSTR(new ImmediateString(value.substring(lastEnd, matcher.start()))));
            }

            compiler.addInstruction(new LOAD(
                    new ImmediateInteger(
                            matcher.group().replace("\\n", "\n").replace("\\r", "\r").replace("\\t", "\t").charAt(0)),
                    Register.R1));
            compiler.addInstruction(new WUTF8());

            lastEnd = matcher.end();
        }

        if (lastEnd < value.length()) {
            compiler.addInstruction(new WSTR(new ImmediateString(value.substring(lastEnd))));
        }
    }

    @Override
    protected void codeByteExp(MethodVisitor mv, DecacCompiler compiler) {

        String processedValue = value.replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\r", "\r");

        mv.visitLdcInsn(processedValue);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("\"" + StringEscapeUtils.escapeJava(value) + "\"");
    }

    @Override
    public void codeGenInst(DecacCompiler compiler) {
        // nothing to do
    }


    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    String prettyPrintNode() {
        return "StringLiteral (" + StringEscapeUtils.escapeJava(value) + ")";
    }

}
