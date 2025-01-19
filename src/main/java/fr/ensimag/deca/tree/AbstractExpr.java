package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Label;
import org.objectweb.asm.MethodVisitor;
import java.io.PrintStream;
import java.lang.reflect.Method;

import org.apache.commons.lang.Validate;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WSTR;

/**
 * Expression, i.e. anything that has a value.
 *
 * @author gl01
 * @date 01/01/2025
 */
public abstract class AbstractExpr extends AbstractInst {
    

    /**
     * @return true if the expression does not correspond to any concrete token
     *         in the source code (and should be decompiled to the empty string).
     */
    boolean isImplicit() {
        return false;
    }

    boolean isSelection() {
        return false;
    }


    /**
     * Get the type decoration associated to this expression (i.e. the type computed
     * by contextual verification).
     */
    public Type getType() {
        return type;
    }

    protected void setType(Type type) {
        Validate.notNull(type);
        this.type = type;
    }

    private Type type;

    @Override
    protected void checkDecoration() {
        if (getType() == null) {
            throw new DecacInternalError("Expression " + decompile() + " has no Type decoration");
        }
    }

    /**
     * Verify the expression for contextual error.
     * 
     * implements non-terminals "expr" and "lvalue"
     * of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler     (contains the "env_types" attribute)
     * @param localEnv
     *                     Environment in which the expression should be checked
     *                     (corresponds to the "env_exp" attribute)
     * @param currentClass
     *                     Definition of the class containing the expression
     *                     (corresponds to the "class" attribute)
     *                     is null in the main bloc.
     * @return the Type of the expression
     *         (corresponds to the "type" attribute)
     */
    public abstract Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;

    /**
     * Verify the expression in right hand-side of (implicit) assignments
     * 
     * implements non-terminal "rvalue" of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler     contains the "env_types" attribute
     * @param localEnv     corresponds to the "env_exp" attribute
     * @param currentClass corresponds to the "class" attribute
     * @param expectedType corresponds to the "type1" attribute
     * @return this with an additional ConvFloat if needed...
     */
    public AbstractExpr verifyRValue(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass,
            Type expectedType)
            throws ContextualError {

        Type typeRvalue = verifyExpr(compiler, localEnv, currentClass);

        if (typeRvalue.subType(expectedType))
            return this;

        if (expectedType.isFloat() && typeRvalue.isInt()) {
            ConvFloat conv = new ConvFloat(this);
            // l'expression this a déja été vérifié précédemment pas besoin de le refaire
            // on initialise juste le type
            conv.setType(compiler.environmentType.FLOAT);
            return conv;
        }

        throw new ContextualError(
                "Error: Illegal RValue type, got " + typeRvalue.toString() + ", expected " + expectedType.toString(),
                getLocation());
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        verifyExpr(compiler, localEnv, currentClass);
    }

    /**
     * Verify the expression as a condition, i.e. check that the type is
     * boolean.
     *
     * @param localEnv
     *                     Environment in which the condition should be checked.
     * @param currentClass
     *                     Definition of the class containing the expression, or
     *                     null in
     *                     the main program.
     */
    void verifyCondition(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        type = verifyExpr(compiler, localEnv, currentClass);
        if (!type.isBoolean()) {
            throw new ContextualError("Error: Expected expression type is 'boolean', got '" + type.toString() + "'",
                    getLocation());
        }
    }

    protected DVal getDVal() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Generate the code for ReadExpr, the result is stored in R1
     * 
     * @param compiler
     */
    protected void codeExp(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Load the value of the expression in Rn
     * 
     * @param compiler
     * @param n
     */
    protected void codeExp(DecacCompiler compiler, int n) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    
    protected void codeByteExp(MethodVisitor mv, DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }
        


    /**
     * Generate code to print the expression
     *
     * @param compiler
     */
    protected void codeGenPrint(DecacCompiler compiler) {
        // on charge la valeur de l'expression dans un registre libre
        codeExp(compiler, 2);

        // on la met dans R1 pour l'afficher
        compiler.addInstruction(new LOAD(Register.getR(compiler,2), Register.R1));
        if (getType().isInt()) {
            compiler.addInstruction(new WINT());
        } else if (getType().isFloat()) {
            compiler.addInstruction(new WFLOAT());
        }
    }

 
    protected void codeGenBytePrint(MethodVisitor mv, DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Generate code to print the expression in hex if "this" is a float
     *
     * @param compiler
     */
    protected void codeGenPrintHex(DecacCompiler compiler) {
        if (getType().isFloat()) {
            // on charge la valeur de l'expression dans un registre libre
            codeExp(compiler, 2);

            // on la met dans R1 pour l'afficher
            compiler.addInstruction(new LOAD(Register.getR(compiler,2), Register.R1));
            compiler.addInstruction(new WFLOATX());
        } else {
            codeGenPrint(compiler);
        }
    }

    protected void codeGenBytePrintHex(MethodVisitor mv,DecacCompiler compiler){
        codeGenBytePrint(mv,compiler);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void codeGenByteInst(MethodVisitor mv, DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }
/* 
    @Override
    protected void codeGenByteInst(MethodVisitor mv, DecacCompiler compiler) 
    */
    

    /**
     * Code generation for boolean expression
     * 
     * @param compiler
     * @param branchIfTrue
     * @param e
     */
    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    protected void codeGenByteBool(MethodVisitor mv, boolean branchIfTrue, org.objectweb.asm.Label e,DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void decompileInst(IndentPrintStream s) {
        decompile(s);
        s.print(";");
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Type t = getType();
        if (t != null) {
            s.print(prefix);
            s.print("type: ");
            s.print(t);
            s.println();
        }
    }
}



