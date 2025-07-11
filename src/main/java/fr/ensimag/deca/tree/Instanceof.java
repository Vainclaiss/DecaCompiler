package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.SEQ;

public class Instanceof extends AbstractExpr{

    final private AbstractExpr expr;
    final private AbstractIdentifier compType;

    private ImmediateInteger dVal;
    private boolean isSubtype;

    public Instanceof(AbstractExpr leftOperand, AbstractIdentifier rightOperand) {
        Validate.notNull(leftOperand);
        Validate.notNull(rightOperand);
        this.expr = leftOperand;
        this.compType = rightOperand;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        Type type1 = expr.verifyExpr(compiler, localEnv, currentClass);
        Type type2 = compType.verifyType(compiler);

        if (!(type1.isClassOrNull() && type2.isClass())) {
            throw new ContextualError("Error: Incompatible types for operation: " + type1 + " instanceof " + type2, getLocation());
        }

        if (type1.subType(type2)) {
            this.dVal = new ImmediateInteger(1);
            this.isSubtype = true;
        }
        else {
            this.dVal = new ImmediateInteger(0);
            this.isSubtype = false;
        }

        setType(compiler.environmentType.BOOLEAN);
        return compiler.environmentType.BOOLEAN;
    }

    @Override
    protected DVal getDVal() {
        return dVal;
    }

    @Override
    protected void codeExp(DecacCompiler compiler,int n) {
        // ne marche pas pour n < 2
        Label loopLabel = new Label("instanceof_loop");
        loopLabel.getAndAddNewSuffixe();
        Label nextTableLabel = new Label("next_vtable");
        nextTableLabel.getAndAddNewSuffixe();
        Label finLabel = new Label("fin_label");
        finLabel.getAndAddNewSuffixe();

        compiler.addComment("calcul de l'operande gauche de instanceof");
        expr.codeExp(compiler, n);
        compiler.addComment("debut de instanceof");
        compiler.addInstruction(new LOAD(Register.getR(compiler, n), Register.R0));
        compiler.addInstruction(new LEA(((ClassType)compType.getType()).getDefinition().getDAddrOperand(), Register.R1));
        compiler.addInstruction(new LOAD(new ImmediateInteger(0),Register.getR(compiler, n))); // on remet à 0

        compiler.addInstruction(new CMP(new NullOperand(), Register.R0));
        compiler.addInstruction(new BEQ(finLabel));
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.R0), Register.R0));

        compiler.addLabel(loopLabel);
        compiler.addInstruction(new CMP(Register.R0, Register.R1));
        compiler.addInstruction(new SEQ(Register.getR(compiler, n)));
        compiler.addInstruction(new BNE(nextTableLabel));
        compiler.addInstruction(new BRA(finLabel));

        compiler.addLabel(nextTableLabel);
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.R0), Register.R0));
        compiler.addInstruction(new CMP(new NullOperand(), Register.R0));
        compiler.addInstruction(new BEQ(finLabel));
        compiler.addInstruction(new BRA(loopLabel));

        compiler.addLabel(finLabel);
    }

    @Override
    protected void codeByteExp(MethodVisitor mv, DecacCompiler compiler) {
        org.objectweb.asm.Label isInstance = new org.objectweb.asm.Label();
        org.objectweb.asm.Label endLabel = new org.objectweb.asm.Label();
    
        expr.codeByteExp(mv, compiler);
    
        String classInternalName = compType.getName().toString().replace('.', '/');
        mv.visitTypeInsn(Opcodes.INSTANCEOF, classInternalName);
    
        mv.visitJumpInsn(Opcodes.IFNE, isInstance);
    
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitJumpInsn(Opcodes.GOTO, endLabel);
    
        mv.visitLabel(isInstance);
        mv.visitInsn(Opcodes.ICONST_1);
    
        mv.visitLabel(endLabel);
    }
    


    @Override
    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e) {
        codeGenBool(compiler, branchIfTrue, e, 2);
    }


    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e, int n) {


        Label loopLabel = new Label("instanceof_loop");
        loopLabel.getAndAddNewSuffixe();
        Label nextTableLabel = new Label("next_vtable");
        nextTableLabel.getAndAddNewSuffixe();
        Label finLabel = new Label("fin_label");
        finLabel.getAndAddNewSuffixe();

        Label eFalse = new Label("e_false");
        eFalse.getAndAddNewSuffixe();
        Label branchLabel = (branchIfTrue) ? e : eFalse;

        expr.codeExp(compiler, n);
        compiler.addInstruction(new LOAD(Register.getR(compiler, n), Register.R0));
        compiler.addInstruction(new LEA(((ClassType)compType.getType()).getDefinition().getDAddrOperand(), Register.R1));

        compiler.addInstruction(new CMP(new NullOperand(), Register.R0));
        compiler.addInstruction(new BEQ(finLabel));
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.R0), Register.R0));


        compiler.addLabel(loopLabel);
        compiler.addInstruction(new CMP(Register.R0, Register.R1));
        compiler.addInstruction(new BNE(nextTableLabel));
        compiler.addInstruction(new BRA(branchLabel));

        compiler.addLabel(nextTableLabel);
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.R0), Register.R0));
        compiler.addInstruction(new CMP(new NullOperand(), Register.R0));
        compiler.addInstruction(new BEQ(finLabel));
        compiler.addInstruction(new BRA(loopLabel));

        compiler.addLabel(finLabel);
        if(!branchIfTrue) {
            compiler.addInstruction(new BRA(e));
            compiler.addLabel(eFalse);
        }

    }

    @Override
protected void codeGenByteBool(MethodVisitor mv, boolean branchIfTrue, org.objectweb.asm.Label e, DecacCompiler compiler) {
    expr.codeByteExp(mv, compiler);

    String classInternalName = compType.getName().toString().replace('.', '/');
    mv.visitTypeInsn(Opcodes.INSTANCEOF, classInternalName);

    if (branchIfTrue) {
        mv.visitJumpInsn(Opcodes.IFNE, e);
    } else {
        mv.visitJumpInsn(Opcodes.IFEQ, e);
    }
}


    


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        s.print(expr.decompile());
        s.print(" instanceof ");
        s.print(compType.decompile());
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s,prefix,false);
        compType.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
        compType.iter(f);
    }

    // @Override
    // protected int getJumpOpcodeForInt() {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'getJumpOpcodeForInt'");
    // }

    // @Override
    // protected int getJumpOpcodeForFloat() {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'getJumpOpcodeForFloat'");
    // }

}
