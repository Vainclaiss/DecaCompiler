package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Full if/else if/else statement.
 *
 * @author gl01
 * @date 01/01/2025
 */
public class IfThenElse extends AbstractInst {
    
    private final AbstractExpr condition; 
    private final ListInst thenBranch;
    private ListInst elseBranch;

    public IfThenElse(AbstractExpr condition, ListInst thenBranch, ListInst elseBranch) {
        Validate.notNull(condition);
        Validate.notNull(thenBranch);
        Validate.notNull(elseBranch);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public void setElseBranch(ListInst elseBranch) {
        this.elseBranch = elseBranch;
    }

    public AbstractExpr getCondition() {
        return condition;
    }

    public ListInst getThenBranch() {
        return thenBranch;
    }

    public ListInst getElseBranch() {
        return elseBranch;
    }

    @Override
    public boolean isIfThenElse() {
        return true;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        Label finIf = new Label("fin_if");
        finIf.getAndAddNewSuffixe();
    
        IfThenElse currIf = this;
        boolean hasElseBranch = currIf.getElseBranch().size() > 0;
    
        while ((currIf.getElseBranch().size() == 1) && currIf.getElseBranch().getList().get(0).isIfThenElse()) {
            Label elseLabel = new Label("else");
            elseLabel.getAndAddNewSuffixe();
    
            IfThenElse nextIf = (IfThenElse) currIf.getElseBranch().getList().get(0);
            boolean hasNextElseBranch = nextIf.getElseBranch().size() > 0;
    
            currIf.getCondition().codeGenBool(compiler, false, elseLabel);
            currIf.getThenBranch().codeGenListInst(compiler);
            compiler.addInstruction(new BRA(finIf));
            compiler.addLabel(elseLabel);
    
            currIf = nextIf;
            hasElseBranch = hasNextElseBranch;
        }
    
        Label elseLabel = new Label("else");
        elseLabel.getAndAddNewSuffixe();
    
        currIf.getCondition().codeGenBool(compiler, false, hasElseBranch ? elseLabel : finIf);
        currIf.getThenBranch().codeGenListInst(compiler);
        if (hasElseBranch) {
            compiler.addInstruction(new BRA(finIf));
            compiler.addLabel(elseLabel);
            currIf.getElseBranch().codeGenListInst(compiler);
        }
    
        // Ajoute l'étiquette de fin
        compiler.addLabel(finIf);
    }
    

    @Override
    public void decompile(IndentPrintStream s) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        condition.iter(f);
        thenBranch.iter(f);
        elseBranch.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        thenBranch.prettyPrint(s, prefix, false);
        elseBranch.prettyPrint(s, prefix, true);
    }
}
