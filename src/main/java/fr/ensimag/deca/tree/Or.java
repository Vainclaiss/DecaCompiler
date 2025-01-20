package fr.ensimag.deca.tree;

import org.objectweb.asm.MethodVisitor;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;

/**
 *
 * @author gl01
 * @date 01/01/2025
 */
public class Or extends AbstractOpBool {

    public Or(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e) {
        And cond = new And(new Not(getLeftOperand()), new Not(getRightOperand()));
        cond.codeGenBool(compiler, !branchIfTrue, e);
    }

    @Override
    protected String getOperatorName() {
        return "||";
    }

    @Override
    protected void codeGenByteBool(MethodVisitor mv, boolean branchIfTrue, org.objectweb.asm.Label e,DecacCompiler compiler)  {
        if (branchIfTrue) {
           
            org.objectweb.asm.Label skipRight = new org.objectweb.asm.Label();
    
            getLeftOperand().codeGenByteBool(mv, true, e,compiler);
    
          
            getRightOperand().codeGenByteBool(mv, true, e,compiler);
    
    
        } else {
         
    
            org.objectweb.asm.Label skipRight = new org.objectweb.asm.Label();
            getLeftOperand().codeGenByteBool(mv,  true, skipRight,compiler);
            
            getRightOperand().codeGenByteBool(mv,  false, e,compiler);
    
            mv.visitLabel(skipRight);
        }
    }
    
    



}
