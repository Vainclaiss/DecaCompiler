package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.REM;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.execerrors.ZeroDivisionError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl01
 * @date 01/01/2025
 */
public class Modulo extends AbstractOpArith {

    public Modulo(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler, DVal op1, GPRegister r) {
        compiler.addInstruction(new REM(op1, r));
        if (!compiler.getCompilerOptions().getSkipExecErrors()) {
            compiler.addExecError(ZeroDivisionError.INSTANCE);
            compiler.addInstruction(new BOV(ZeroDivisionError.INSTANCE.getLabel()));
        }
    }

    
    @Override
    protected void codeGenByteInst(MethodVisitor mv, DecacCompiler compiler)  {
        // Evaluate and store the right operand
        getRightOperand().codeByteExp(mv, compiler);
        int rightVarIndex = compiler.allocateLocalIndex();
        if (getType().isInt()) {
            mv.visitVarInsn(Opcodes.ISTORE, rightVarIndex);
        } else if (getType().isFloat()) {
            mv.visitVarInsn(Opcodes.FSTORE, rightVarIndex);
        } else {
            throw new UnsupportedOperationException("Unsupported type: " + getType());
        }
    
        // Evaluate the left operand (leaves it on the stack)
        getLeftOperand().codeByteExp(mv, compiler);
    
        // Load the right operand and perform modulo operation
        if (getType().isInt()) {
            mv.visitVarInsn(Opcodes.ILOAD, rightVarIndex);
            mv.visitInsn(Opcodes.IREM);
        } else if (getType().isFloat()) {
            mv.visitVarInsn(Opcodes.FLOAD, rightVarIndex);
            mv.visitInsn(Opcodes.FREM);
        } else {
            throw new UnsupportedOperationException("Unsupported type: " + getType());
        }
    }
    

    @Override
    protected Type getTypeBinaryOp(DecacCompiler compiler, Type type1, Type type2) throws ContextualError {
        if (type1.isInt() && type2.isInt()) {
            return type1;
        }

        throw new ContextualError(
                "Error: Incompatible types for arithmetic operation: " + type1 + " " + getOperatorName() + " " + type2,
                getLocation());
    }

    @Override
    protected String getOperatorName() {
        return "%";
    }

}
