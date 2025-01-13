package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
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
public abstract class AbstractOpCmp extends AbstractBinaryExpr {

    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        codeExp(compiler, 2);

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
    protected void codeGenBytePrint(MethodVisitor mv){
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected Type getTypeBinaryOp(DecacCompiler compiler, Type type1, Type type2) throws ContextualError {
        if ((type1.isInt() && type2.isInt()) || (type1.isFloat() && type2.isFloat()) || (type1.isBoolean() && type2.isBoolean())) {
            return compiler.environmentType.BOOLEAN;
        }

        if (type1.isInt() && type2.isFloat()) {
            ConvFloat convLeft = new ConvFloat(getLeftOperand());
            convLeft.setType(compiler.environmentType.FLOAT);
            setLeftOperand(convLeft);

            return compiler.environmentType.BOOLEAN;
        }

        if (type1.isFloat() && type2.isInt()) {
            ConvFloat convRight = new ConvFloat(getRightOperand());
            convRight.setType(compiler.environmentType.FLOAT);
            setRightOperand(convRight);

            return compiler.environmentType.BOOLEAN;
        }

        // A FAIRE: gerer le cas de eq et neq pour les classes cf p76
        throw new ContextualError(
                "Error: Incompatible types for comparison: " + type1 + " " + getOperatorName() + " " + type2,
                getLocation());
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler, DVal op1, GPRegister r) {
        compiler.addInstruction(new CMP(op1, r));
    }
    @Override
    protected void codeGenByteInst(MethodVisitor mv) {
        
        getLeftOperand().codeByteExp(mv);
    
        getRightOperand().codeByteExp(mv);
    
        org.objectweb.asm.Label labelTrue = new org.objectweb.asm.Label();
        org.objectweb.asm.Label labelEnd  = new org.objectweb.asm.Label();
    
       
        if (getLeftOperand().getType().isInt()) {
            
            mv.visitJumpInsn(getJumpOpcodeForInt(), labelTrue);
                
                    } else if (getLeftOperand().getType().isFloat()) {
                        mv.visitInsn(Opcodes.FCMPG);
                
                        mv.visitJumpInsn(getJumpOpcodeForFloat(), labelTrue);
                
                    } else {
                        throw new UnsupportedOperationException(
                            "AbstractOpCmp: only int or float comparisons are supported."
                        );
                    }
                
                    mv.visitInsn(Opcodes.ICONST_0);
                    mv.visitJumpInsn(Opcodes.GOTO, labelEnd);
                
                    mv.visitLabel(labelTrue);
                    mv.visitInsn(Opcodes.ICONST_1);
                
                    mv.visitLabel(labelEnd);
                }
            
                protected abstract int getJumpOpcodeForInt();
                protected abstract int getJumpOpcodeForFloat();
    

    // @Override
    // public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
    // ClassDefinition currentClass) throws ContextualError {
    // throw new UnsupportedOperationException("not yet implemented");
    // }

}
