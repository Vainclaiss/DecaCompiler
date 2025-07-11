package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.STORE;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author gl01
 * @date 01/01/2025
 */
public class Initialization extends AbstractInitialization {

    public AbstractExpr getExpression() {
        return expression;
    }

    private AbstractExpr expression;

    public void setExpression(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    public Initialization(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    @Override
    protected void verifyInitialization(DecacCompiler compiler, Type t,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {

        setExpression(expression.verifyRValue(compiler, localEnv, currentClass, t));
    }

    @Override
    protected void codeGenInitialization(DecacCompiler compiler, DAddr adresse) {
        expression.codeExp(compiler, 2);

        compiler.addInstruction(new STORE(Register.getR(compiler,2), adresse));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(" = " + expression.decompile());
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expression.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expression.prettyPrint(s, prefix, true);
    }
    @Override
    protected void codeGenByteInitialization(MethodVisitor mv, int localIndex, DecacCompiler compiler)  {
       
      
            expression.codeByteExp(mv, compiler);
        
    
        Type exprType = expression.getType();
    
        if (exprType.isInt()) {
            mv.visitVarInsn(Opcodes.ISTORE, localIndex);
        } else if (exprType.isFloat()) {
            mv.visitVarInsn(Opcodes.FSTORE, localIndex);
        } else if (exprType.isBoolean()) {
            mv.visitVarInsn(Opcodes.ISTORE, localIndex);
        } else if (exprType.isString() || exprType.isClass() || exprType.isNull()) {
            mv.visitVarInsn(Opcodes.ASTORE, localIndex);
        } else {
            throw new UnsupportedOperationException("Unsupported type for initialization: " + exprType);
        }
    }
    

    }
    
    

