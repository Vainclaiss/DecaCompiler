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
    protected void codeGenByteInst(MethodVisitor mv,DecacCompiler compiler){
        
        getLeftOperand().codeByteExp(mv,compiler);
        getRightOperand().codeByteExp(mv,compiler);

        if (getType().isInt()) {

            mv.visitInsn(Opcodes.IREM);

        } else if (getType().isFloat()) {

            mv.visitInsn(Opcodes.FREM);
            
        } else {
            throw new DecacInternalError(
                "Plus: unsupported type formodulo: " + getType());
        }
    }

    

    @Override
    protected Type getTypeBinaryOp(DecacCompiler compiler, Type type1, Type type2) throws ContextualError {
        if (type1.isInt() && type2.isInt()) {
            return type1;
        }

        throw new ContextualError(
                "Error: Incompatible types for arithmetic operation: " + type1 + getOperatorName() + type2,
                getLocation());
    }

    @Override
    protected String getOperatorName() {
        return "%";
    }

}
