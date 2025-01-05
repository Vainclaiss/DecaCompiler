package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
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
    protected void codeGenPrint(DecacCompiler compiler) {
        Label printTrue = new Label("print_true");
        String suffixeIdPrintTrue = printTrue.getAndAddNewSuffixe();

        Label finPrint = new Label("fin_print");
        finPrint.addSuffixe(suffixeIdPrintTrue);

        codeGenBool(compiler, true, printTrue);
        compiler.addInstruction(new WSTR("false"));
        compiler.addInstruction(new BRA(finPrint));

        compiler.addLabel(printTrue);
        compiler.addInstruction(new WSTR("true"));

        compiler.addLabel(finPrint);
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        Label e = new Label("binaryBool_eval_true");
        String suffixe = e.getAndAddNewSuffixe();

        //si l'expression est évaluée à vrai on jump a not_eval_true sinon on load 0 dans Rn
        codeGenBool(compiler, true, e);
        compiler.addInstruction(new LOAD(0, Register.getR(n)));
        Label skipEvalTrue = new Label("skip_eval_true");
        skipEvalTrue.addSuffixe(suffixe);
        compiler.addInstruction(new BRA(skipEvalTrue));

        compiler.addLabel(e);
        compiler.addInstruction(new LOAD(1, Register.getR(n)));
        compiler.addLabel(skipEvalTrue);        
    }

    @Override
    protected Type getTypeBinaryOp(DecacCompiler compiler, Type type1, Type type2) throws ContextualError {
        if (type1.isBoolean() && type2.isBoolean()) {
            return compiler.environmentType.BOOLEAN;
        }
        
        throw new ContextualError("Incompatible types for boolean operation: " + type1 + " " + getOperatorName() + " " + type2, getLocation());
    }

    // @Override
    // public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
    //         ClassDefinition currentClass) throws ContextualError {
    //     throw new UnsupportedOperationException("not yet implemented");
    // }

}
