package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl01
 * @date 01/01/2025
 */
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        Label e = new Label("not_eval_true");
        String suffixe = e.getAndAddNewSuffixe();
        // si l'expression est évaluée à vrai on jump a not_eval_true sinon on load 0
        // dans Rn
        codeGenBool(compiler, true, e);
        compiler.addInstruction(new LOAD(0, Register.getR(compiler,n)));
        Label skipEvalTrue = new Label("skip_eval_true");
        skipEvalTrue.addSuffixe(suffixe);
        compiler.addInstruction(new BRA(skipEvalTrue));

        compiler.addLabel(e);
        compiler.addInstruction(new LOAD(1, Register.getR(compiler,n)));
        compiler.addLabel(skipEvalTrue);
    }

    @Override
    protected Type getTypeUnaryOp(DecacCompiler compiler, Type type) throws ContextualError {
        if (type.isBoolean()) {
            return type;
        }

        throw new ContextualError(
                "Error: Incompatible type for operator '" + getOperatorName() + "' and type '" + type + "'",
                getLocation());
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e) {
        getOperand().codeGenBool(compiler, !branchIfTrue, e);
    }

 

    @Override
protected void codeByteExp(MethodVisitor mv,DecacCompiler compiler)  {

    org.objectweb.asm.Label operandTrue = new org.objectweb.asm.Label();
    org.objectweb.asm.Label endLabel    = new org.objectweb.asm.Label();

    getOperand().codeGenByteBool(mv, true, operandTrue,compiler);

    mv.visitInsn(Opcodes.ICONST_1);

    mv.visitJumpInsn(Opcodes.GOTO, endLabel);

    mv.visitLabel(operandTrue);
    mv.visitInsn(Opcodes.ICONST_0);

    mv.visitLabel(endLabel);
}


@Override
protected void codeGenByteBool(MethodVisitor mv, boolean branchIfTrue, org.objectweb.asm.Label e,DecacCompiler compiler)  {
   
    getOperand().codeGenByteBool(mv, !branchIfTrue, e,compiler);
}


    @Override
    protected String getOperatorName() {
        return "!";
    }
}
