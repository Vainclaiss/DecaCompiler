package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.OPP;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Conversion of an int into a float. Used for implicit conversions.
 * 
 * @author gl01
 * @date 01/01/2025
 */
public class ConvFloat extends AbstractUnaryExpr {
    public ConvFloat(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) {
        // operand est de type int par construction
        setType(compiler.environmentType.FLOAT);
        return compiler.environmentType.FLOAT;
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        getOperand().codeExp(compiler, n);
        compiler.addInstruction(new FLOAT(Register.getR(compiler,n), Register.getR(compiler,n)));
    }

    @Override
    protected void codeByteExp(MethodVisitor mv,DecacCompiler compiler)   {
        
        getOperand().codeByteExp(mv,compiler);
      
        mv.visitInsn(Opcodes.I2F);
    }

    @Override
    protected String getOperatorName() {
        return "/* conv float */";
    }
}
