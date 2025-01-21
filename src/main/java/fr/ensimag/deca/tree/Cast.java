package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.execerrors.IncompatibleCastError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.INT;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Cast extends AbstractExpr {

    final private AbstractIdentifier type;
    private AbstractExpr expr;

    public Cast(AbstractIdentifier type, AbstractExpr expr) {
        this.type = type;
        this.expr = expr;
    }

    @Override
    protected DVal getDVal() {
        return expr.getDVal();
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        Type type1 = type.verifyType(compiler);
        Type type2 = expr.verifyExpr(compiler, localEnv, currentClass);

        if (type1.isVoid()) {
            throw new ContextualError("Error: Cannot cast void", getLocation());
        }

        try {
            expr.verifyRValue(compiler, localEnv, currentClass, type1);
        } catch (ContextualError typeErr) {

            if (!type1.subType(type2) && !(type1.isFloat() && type2.isInt()) && !(type1.isInt() && type2.isFloat())) {
                throw new ContextualError("Error: Cannot cast " + type2.getName() + " to " + type1.getName(),
                        getLocation());
            }

        }
        
        setType(type1);
        return type1;
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        int indexR = (n>=2) ? n : 2;
        compiler.addComment("debut du cast " + expr.getType().toString() + " vers " + type.getName().getName());
        if (type.getType().isInt()) {
            expr.codeExp(compiler, indexR);
            compiler.addInstruction(new INT(Register.getR(indexR), Register.getR(indexR)));
        }
        else if (type.getType().isFloat()) {
            expr.codeExp(compiler, indexR);
            compiler.addInstruction(new FLOAT(Register.getR(indexR), Register.getR(indexR)));
        }
        else if (type.getType().isClass() && !expr.getType().isNull()) {

            if (!compiler.getCompilerOptions().getSkipExecErrors()) {
                compiler.addExecError(IncompatibleCastError.INSTANCE);
                //compiler.addInstruction(new BOV(OverflowError.INSTANCE.getLabel()));
            }

            (new Instanceof(expr, type)).codeGenBool(compiler, false, IncompatibleCastError.INSTANCE.getLabel(), indexR);
        }
        else {
            expr.codeExp(compiler, n);
        }
        if (indexR != n) {
            compiler.addInstruction(new LOAD(Register.getR(indexR), Register.getR(compiler, n)));
        }
        compiler.addComment("fin du cast " + expr.getType().toString() + " vers " + type.getName().getName());
    }

    @Override
    protected void codeByteExp(MethodVisitor mv, DecacCompiler compiler) {
        expr.codeByteExp(mv, compiler);
    
        if (type.getType().isInt()) {
            if (expr.getType().isFloat()) {
                mv.visitInsn(Opcodes.F2I); 
            }
        } else if (type.getType().isFloat()) {
            if (expr.getType().isInt()) {
                mv.visitInsn(Opcodes.I2F); 
            }
        } else if (type.getType().isClass() && !expr.getType().isNull()) {
            mv.visitInsn(Opcodes.DUP);
            
            org.objectweb.asm.Label castSuccess = new org.objectweb.asm.Label();
            org.objectweb.asm.Label castFail = new org.objectweb.asm.Label();
    
            mv.visitJumpInsn(Opcodes.IFNONNULL, castSuccess);
    
            mv.visitLabel(castFail);
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("Error: Incompatible cast");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            mv.visitInsn(Opcodes.POP); 
            mv.visitInsn(Opcodes.ACONST_NULL); 
    
            mv.visitLabel(castSuccess);
            mv.visitTypeInsn(Opcodes.CHECKCAST, type.getName().getName().replace('.', '/'));
        }
    }
    


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(" + type.getName() + ") (" + expr.decompile() + ")");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        expr.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        expr.prettyPrint(s, prefix, false);
    }
}