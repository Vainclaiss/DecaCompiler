package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.execerrors.HeapOverflowError;
import fr.ensimag.deca.codegen.execerrors.IOError;
import fr.ensimag.deca.codegen.execerrors.OverflowError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.NEW;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.RFLOAT;
import fr.ensimag.ima.pseudocode.instructions.RINT;
import fr.ensimag.ima.pseudocode.instructions.STORE;

import java.io.PrintStream;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 *
 * @author gl01
 * @date 01/01/2025
 */
public class New extends AbstractExpr {

    final private AbstractIdentifier name;

    public New(AbstractIdentifier name) {
        this.name = name;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        Type type = name.verifyType(compiler);
        if (!type.isClass()) {
            throw new ContextualError("Error: Cannot create an object from a non class type", getLocation());
        }
        
        name.setDefinition(type.asClassType("Error : cast failed from Type to ClassType", getLocation()).getDefinition());
        setType(type);
        return type;
    }

    @Override
    protected void codeExp(DecacCompiler compiler) {
        codeExp(compiler, 0);
    }

    @Override
    public DVal getDVal() {
        return Register.R0;
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {

        ClassDefinition nameDef = name.getClassDefinition();
        compiler.addInstruction(new NEW(nameDef.getNumberOfFields()+1, Register.getR(compiler,n)));

        if (!compiler.getCompilerOptions().getSkipExecErrors()) {
            compiler.addExecError(HeapOverflowError.INSTANCE);
            compiler.addInstruction(new BOV(HeapOverflowError.INSTANCE.getLabel()));
        }

        compiler.addInstruction(new LEA(nameDef.getOperand(), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(0, Register.getR(compiler,n))));
        compiler.addInstruction(new PUSH(Register.getR(compiler,n)));
        compiler.addInstruction(new BSR(new Label("init." + nameDef.getType().toString())));
        compiler.addInstruction(new POP(Register.getR(compiler,n)));
    }

    @Override
protected void codeByteExp(MethodVisitor mv, DecacCompiler compiler) {
    // 1) Retrieve the internal name for the class.
    //    For instance, if the class is "mypackage.MyClass",
    //    we want "mypackage/MyClass" for ASM.
    ClassDefinition classDef = name.getClassDefinition();
    // You might store or compute the internal name somewhere:
    // e.g. classDef.getInternalName() or do a string transform:
    String internalName = classDef.getInternalName(); 
    // or:
    // String internalName = name.getName().toString().replace('.', '/');

    // 2) Emit a NEW instruction to create an uninitialized object.
    mv.visitTypeInsn(Opcodes.NEW, internalName);

    // 3) DUP so that we have both a reference for the constructor call
    //    *and* a copy remains on the stack as the expression result.
    mv.visitInsn(Opcodes.DUP);

    // 4) Invoke the class's default constructor <init>(). 
    //    If your language supports user-defined constructors, you'd adapt the signature.
    mv.visitMethodInsn(
        Opcodes.INVOKESPECIAL, 
        internalName, 
        "<init>",
        "()V",  // no-arg constructor
        false
    );

    // The newly constructed object is now on top of the stack as the expression result.
}


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new ");
        name.decompile(s);
        s.print("()");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        name.prettyPrint(s,prefix,false);
    }

}
