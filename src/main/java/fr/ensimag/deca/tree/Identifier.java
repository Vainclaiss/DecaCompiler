package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Deca Identifier
 *
 * @author gl01
 * @date 01/01/2025
 */
public class Identifier extends AbstractIdentifier {

    @Override
    protected void checkDecoration() {
        if (getDefinition() == null) {
            throw new DecacInternalError("Identifier " + this.getName() + " has no attached Definition");
        }
    }

    @Override
    public Definition getDefinition() {
        return definition;
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * ClassDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *                            if the definition is not a class definition.
     */
    @Override
    public ClassDefinition getClassDefinition() {
        try {
            return (ClassDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a class identifier, you can't call getClassDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * MethodDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *                            if the definition is not a method definition.
     */
    @Override
    public MethodDefinition getMethodDefinition() {
        try {
            return (MethodDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a method identifier, you can't call getMethodDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * FieldDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *                            if the definition is not a field definition.
     */
    @Override
    public FieldDefinition getFieldDefinition() {
        try {
            return (FieldDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a field identifier, you can't call getFieldDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * VariableDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *                            if the definition is not a field definition.
     */
    @Override
    public VariableDefinition getVariableDefinition() {
        try {
            return (VariableDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a variable identifier, you can't call getVariableDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * ExpDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *                            if the definition is not a field definition.
     */
    @Override
    public ExpDefinition getExpDefinition() {
        try {
            return (ExpDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a Exp identifier, you can't call getExpDefinition on it");
        }
    }

    @Override
    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    @Override
    public Symbol getName() {
        return name;
    }

    private Symbol name;

    public Identifier(Symbol name) {
        Validate.notNull(name);
        this.name = name;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        return verifyLValue(localEnv);
    }

    public Definition verifyIdentifier(EnvironmentExp localEnv) throws ContextualError {

        Definition nameDef = localEnv.get(name);
        if (nameDef == null) {
            throw new ContextualError("Error: Identifier '" + name.toString() + "' is undefined",
                    getLocation());
        }

        return nameDef;
    }

    /**
     * Implements non-terminal "lvalue_ident" of [SyntaxeContextuelle] in passe 3
     * 
     * @param localEnv
     * @return
     * @throws ContextualError
     */
    public Type verifyLValue(EnvironmentExp localEnv) throws ContextualError {

        Definition nameDef = verifyIdentifier(localEnv);

        if (!(nameDef.isField() || nameDef.isParam() || nameDef.isVar())) {
            throw new ContextualError("Error: LValue identifer must be a field, parameter or variable", getLocation());
        }

        setDefinition(nameDef);
        setType(nameDef.getType());

        return nameDef.getType();
    }

    /**
     * Implements non-terminal "field_ident" of [SyntaxeContextuelle] in passe 3
     * 
     * @param localEnv
     * @return
     * @throws ContextualError
     */
    public FieldDefinition verifyField(EnvironmentExp localEnv) throws ContextualError {

        Definition nameDef = verifyIdentifier(localEnv);

        if (!nameDef.isField()) {
            throw new ContextualError("Error: Selection identifer must be a field", getLocation());
        }

        setDefinition(nameDef);
        setType(nameDef.getType());

        return getFieldDefinition();
    }

    /**
     * Implements non-terminal "method_ident" of [SyntaxeContextuelle] in passe 3
     * 
     * @param localEnv
     * @return
     * @throws ContextualError
     */
    public MethodDefinition verifyMethod(EnvironmentExp localEnv) throws ContextualError {

        Definition nameDef = verifyIdentifier(localEnv);

        if (!nameDef.isMethod()) {
            throw new ContextualError("Error: MethodCall identifer must be a method", getLocation());
        }

        setDefinition(nameDef);
        setType(nameDef.getType());

        return getMethodDefinition();
    }

    /**
     * Implements non-terminal "type" of [SyntaxeContextuelle] in the 3 passes
     * 
     * @param compiler contains "env_types" attribute
     */
    @Override
    public Type verifyType(DecacCompiler compiler) throws ContextualError {
        TypeDefinition typeDefName = compiler.environmentType.defOfType(name);
        if (typeDefName == null) {
            throw new ContextualError("Error: Type '" + name.toString() + "' is not defined", getLocation());
        }
        Type type = typeDefName.getType();
        definition = new TypeDefinition(type, typeDefName.getLocation());

        setType(type);
        return type;
    }

    @Override
    protected DVal getDVal() {
        return getExpDefinition().getOperand();
    }

    @Override
    protected void codeExp(DecacCompiler compiler) {
        if (getDefinition().isField()) {
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R0));
        }
        // nothing to do, used for Assign codeGenInst
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        if (getDefinition().isField()) {
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R0));
        }
        compiler.addInstruction(new LOAD(getDVal(), Register.getR(compiler, n)));
    }

    @Override
protected void codeByteExp(MethodVisitor mv,DecacCompiler compiler) {
    
    int localIndex = getVariableDefinition().getLocalIndex();

    Type t = getType();

    if (t.isInt()) {
        mv.visitVarInsn(Opcodes.ILOAD, localIndex);
    } else if (t.isFloat()) {
        mv.visitVarInsn(Opcodes.FLOAD, localIndex);
    } else if (t.isBoolean()){
        mv.visitVarInsn(Opcodes.ILOAD,localIndex);
    }
    else{
        throw new UnsupportedOperationException(
            "Identifier: unsupported type for codeGenByteExp: " + t
        );

    }
}



    @Override
    protected void codeGenBytePrint(MethodVisitor mv,DecacCompiler compiler) {
        Type type = getType();
        mv.visitFieldInsn(
            Opcodes.GETSTATIC,
            "java/lang/System",
            "out",
            "Ljava/io/PrintStream;"
        );
    
        int localIndex = getVariableDefinition().getLocalIndex();
        
        if (type.isInt()) {
            mv.visitVarInsn(Opcodes.ILOAD, localIndex);
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(I)V",
            false
        );
    }
        else if(type.isFloat()){
            mv.visitVarInsn(Opcodes.FLOAD, localIndex);
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(F)V",
                false
            );

        }
}



    


    /**
     * Generate code to print the expression
     *
     * @param compiler
     */
    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        compiler.addInstruction(new LOAD(getDVal(), Register.R1));
        Type type = getType();
        if (type.isInt()) {
            compiler.addInstruction(new WINT());
        } else if (type.isFloat()) {
            compiler.addInstruction(new WFLOAT());
        } else {
            throw new UnsupportedOperationException("Print of this type identifier not yet implemented");
        }
    }

        /**
     * Generate code to print the expression
     *
     * @param compiler
     */
    @Override
    protected void codeGenPrintHex(DecacCompiler compiler) {
        Type type = getType();
        if (type.isFloat()) {
            compiler.addInstruction(new WFLOATX());
        }
        else {
            codeGenPrint(compiler);
        }
    }

    @Override
    protected void codeGenBytePrintHex(MethodVisitor mv,DecacCompiler compiler) {
        Type type= getType();
        
        if(type.isFloat()){

            mv.visitFieldInsn(
                Opcodes.GETSTATIC,
                "java/lang/System",
                "out",
                "Ljava/io/PrintStream;"
            );
            
            int localIndex = getVariableDefinition().getLocalIndex();


            mv.visitVarInsn(Opcodes.FLOAD, localIndex);

            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "java/lang/Float",      
                "toHexString",           
                "(F)Ljava/lang/String;", // prend un float et retourne un string
                false
            );

            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(Ljava/lang/String;)V",
                false
            );
            
        }
    }
    
    
    private Definition definition;

    @Override
    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e) {
        // TODO : gerer le cas des selection, method call etc
        compiler.addInstruction(new LOAD(getDVal(), Register.R0));
        compiler.addInstruction(new CMP(1, Register.R0));
        if (branchIfTrue) {
            compiler.addInstruction(new BEQ(e));
        } else {
            compiler.addInstruction(new BNE(e));
        }
    }

    @Override
protected void codeGenByteBool(MethodVisitor mv, boolean branchIfTrue,
                               org.objectweb.asm.Label e, DecacCompiler compiler) {
    codeByteExp(mv, compiler);

    if (branchIfTrue) {
        mv.visitJumpInsn(Opcodes.IFNE, e);
    } else {
        mv.visitJumpInsn(Opcodes.IFEQ, e);
    }
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
    public void decompile(IndentPrintStream s) {
        s.print(name.toString());
    }

    @Override
    String prettyPrintNode() {
        return "Identifier (" + getName() + ")";
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Definition d = getDefinition();
        if (d != null) {
            s.print(prefix);
            s.print("definition: ");
            s.print(d);
            s.println();
        }
    }

}
