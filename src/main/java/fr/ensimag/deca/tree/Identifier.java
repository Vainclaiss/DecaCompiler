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
import fr.ensimag.ima.pseudocode.Register;
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

        if (localEnv.get(name) == null)
            throw new ContextualError("Error: Identifier " + name.toString() + " must have a definition",
                    getLocation());
        setDefinition(localEnv.get(name));
        setType(getDefinition().getType());

        return getDefinition().getType();
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
            throw new ContextualError("Error: Type " + name.toString() + " is not defined", getLocation());
        }
        Type type = typeDefName.getType();
        definition = new TypeDefinition(type, typeDefName.getLocation());

        return type;
    }

    @Override
    protected DVal getDVal() {
        return getVariableDefinition().getOperand();
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        compiler.addInstruction(new LOAD(getDVal(), Register.getR(n)));
    }

    @Override
protected void codeByteExp(MethodVisitor mv) {
    int localIndex = getVariableDefinition().getLocalIndex();

    Type t = getType();

    if (t.isInt()) {
        mv.visitVarInsn(Opcodes.ILOAD, localIndex);
    } else if (t.isFloat()) {
        mv.visitVarInsn(Opcodes.FLOAD, localIndex);
    } else {
 
        throw new UnsupportedOperationException(
            "Identifier: unsupported type for codeGenByteExp: " + t
        );
    }
}


    @Override
    protected void codeGenBytePrint(MethodVisitor mv) {
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
    
    
    private Definition definition;

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
