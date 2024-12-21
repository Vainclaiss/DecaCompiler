package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
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
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e) {
        getOperand().codeGenBool(compiler, !branchIfTrue, e);
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        Label e = new Label("not_eval_true");
        e.addSuffixeUnique();
        //si l'expression est évaluée à vrai on jump a not_eval_true sinon on load 0 dans Rn
        codeGenBool(compiler, true, e);
        compiler.addInstruction(new LOAD(0, Register.getR(n)));
        Label skipEvalTrue = new Label("skip_eval_true");
        skipEvalTrue.addSuffixeUnique();
        compiler.addInstruction(new BRA(skipEvalTrue));

        compiler.addLabel(e);
        compiler.addInstruction(new LOAD(1, Register.getR(n)));
        compiler.addLabel(skipEvalTrue);        
    }
    
    @Override
    protected String getOperatorName() {
        return "!";
    }
}
