package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
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
public abstract class AbstractOpBool extends AbstractBinaryExpr {

    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        Label e = new Label("binaryBool_eval_true");
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
protected void codeByteExp(MethodVisitor mv,DecacCompiler compiler) {
    org.objectweb.asm.Label evalTrue = new org.objectweb.asm.Label();
    org.objectweb.asm.Label endLabel = new org.objectweb.asm.Label();

    
    codeGenByteBool(mv, /* branchIfTrue */ true, evalTrue,compiler);

    mv.visitInsn(Opcodes.ICONST_0);
    mv.visitJumpInsn(Opcodes.GOTO, endLabel);

    mv.visitLabel(evalTrue);
    mv.visitInsn(Opcodes.ICONST_1);

    // End of logic
    mv.visitLabel(endLabel);
}

    @Override
    protected Type getTypeBinaryOp(DecacCompiler compiler, Type type1, Type type2) throws ContextualError {
        if (type1.isBoolean() && type2.isBoolean()) {
            return compiler.environmentType.BOOLEAN;
        }

        throw new ContextualError(
                "Error: Incompatible types for boolean operation: " + type1 + " " + getOperatorName() + " " + type2,
                getLocation());
    }

    // @Override
    // public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
    // ClassDefinition currentClass) throws ContextualError {
    // throw new UnsupportedOperationException("not yet implemented");
    // }

}
