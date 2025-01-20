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
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 *
 * @author gl01
 * @date 01/01/2025
 */
public class While extends AbstractInst {
    private AbstractExpr condition;
    private ListInst body;

    public AbstractExpr getCondition() {
        return condition;
    }

    public ListInst getBody() {
        return body;
    }

    public While(AbstractExpr condition, ListInst body) {
        Validate.notNull(condition);
        Validate.notNull(body);
        this.condition = condition;
        this.body = body;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler, Label finLabel) {
        Label whileCond = new Label("while_cond");
        whileCond.getAndAddNewSuffixe();
        compiler.addInstruction(new BRA(whileCond));

        Label debut = new Label("while_debut");
        debut.getAndAddNewSuffixe();
        compiler.addLabel(debut);
        body.codeGenListInst(compiler, finLabel);

        compiler.addLabel(whileCond);
        condition.codeGenBool(compiler, true, debut);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        Label whileCond = new Label("while_cond");
        whileCond.getAndAddNewSuffixe();
        compiler.addInstruction(new BRA(whileCond));

        Label debut = new Label("while_debut");
        debut.getAndAddNewSuffixe();
        compiler.addLabel(debut);
        body.codeGenListInst(compiler);

        compiler.addLabel(whileCond);
        condition.codeGenBool(compiler, true, debut);
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {

        condition.verifyCondition(compiler, localEnv, currentClass);
        body.verifyListInst(compiler, localEnv, currentClass, returnType);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("while (");
        getCondition().decompile(s);
        s.println(") {");
        s.indent();
        getBody().decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        condition.iter(f);
        body.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, true);
    }

    @Override
    protected void codeGenByteInst(MethodVisitor mv,DecacCompiler compiler)  {
        org.objectweb.asm.Label whileStart = new org.objectweb.asm.Label();
        org.objectweb.asm.Label whileEnd = new org.objectweb.asm.Label();
    
        mv.visitLabel(whileStart); // on visite le start
    
        condition.codeGenByteBool(mv, false, whileEnd,compiler); //si la condition est fausse on sort du loop
    
        body.codeGenListInstByte(mv,compiler); // on genere le bytecode pour le contenu du loop
    
        mv.visitJumpInsn(Opcodes.GOTO, whileStart); // on va au while start encore
    
        mv.visitLabel(whileEnd);
    }
    
    
    

}
