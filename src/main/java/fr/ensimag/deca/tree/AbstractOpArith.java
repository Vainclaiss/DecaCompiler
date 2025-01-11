package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import org.objectweb.asm.MethodVisitor;

/**
 * Arithmetic binary operations (+, -, /, ...)
 * 
 * @author gl01
 * @date 01/01/2025
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {

    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        // on charge la valeur de l'expression dans un registre libre
        codeExp(compiler, 2);

        // on la met dans R1 pour l'afficher
        compiler.addInstruction(new LOAD(Register.getR(2), Register.R1));
        if (getType().isInt()) {
            compiler.addInstruction(new WINT());
        } else if (getType().isFloat()) {
            compiler.addInstruction(new WFLOAT());
        } else {
            throw new IllegalAccessError("Arithmetic expression must have int or float type");
        }
    }

    @Override
protected void codeGenBytePrint(MethodVisitor mv) {
   
    codeByteExp(mv);

    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

    mv.visitInsn(Opcodes.SWAP);

    if (getType().isInt()) {

        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);

    } else if (getType().isFloat()) {

        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(F)V", false);
    } else {

        throw new IllegalAccessError("Unsupported type for bytecode print: " + getType());
    }
}



    @Override
    protected Type getTypeBinaryOp(DecacCompiler compiler, Type type1, Type type2) throws ContextualError {
        if (type1.isInt() && type2.isInt()) {
            return compiler.environmentType.INT;
        }

        if (type1.isInt() && type2.isFloat()) {
            ConvFloat convLeft = new ConvFloat(getLeftOperand());
            convLeft.setType(compiler.environmentType.FLOAT);
            setLeftOperand(convLeft);

            return compiler.environmentType.FLOAT;
        }

        if (type1.isFloat() && type2.isInt()) {
            ConvFloat convRight = new ConvFloat(getRightOperand());
            convRight.setType(compiler.environmentType.FLOAT);
            setRightOperand(convRight);

            return compiler.environmentType.FLOAT;
        }

        if (type1.isFloat() && type2.isFloat()) {
            return compiler.environmentType.FLOAT;
        }

        throw new ContextualError(
                "Error: Incompatible types for arithmetic operation: " + type1 + " " + getOperatorName() + " " + type2,
                getLocation());
    }

    // @Override
    // public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
    // ClassDefinition currentClass) throws ContextualError {
    // throw new UnsupportedOperationException("not yet implemented");
    // }
}
