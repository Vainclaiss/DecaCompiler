package fr.ensimag.deca.tree;

import org.objectweb.asm.MethodVisitor;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.ima.pseudocode.Label;

/**
 *
 * @author gl01
 * @date 01/01/2025
 */
public class And extends AbstractOpBool {

    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e) {
        if (branchIfTrue) {
            Label eFin = new Label(e.toString() + "_fin");
            eFin.getAndAddNewSuffixe();
            getLeftOperand().codeGenBool(compiler, false, eFin);
            getRightOperand().codeGenBool(compiler, true, e);
            compiler.addLabel(eFin);
        } else {
            getLeftOperand().codeGenBool(compiler, false, e);
            getRightOperand().codeGenBool(compiler, false, e);
        }
    }

    @Override
    protected String getOperatorName() {
        return "&&";
    }

    @Override
    protected void codeGenByteBool(MethodVisitor mv, boolean branchIfTrue, org.objectweb.asm.Label e,DecacCompiler compiler)  {
       
        if (branchIfTrue) {
          
            org.objectweb.asm.Label skipRight = new org.objectweb.asm.Label();
    
            getLeftOperand().codeGenByteBool(mv, false, skipRight,compiler);
    
            getRightOperand().codeGenByteBool(mv, true, e,compiler);
    
            mv.visitLabel(skipRight);
        } else {

            getLeftOperand().codeGenByteBool(mv, false, e,compiler);
            getRightOperand().codeGenByteBool(mv, false, e,compiler);
        }
    }

    
    
}
