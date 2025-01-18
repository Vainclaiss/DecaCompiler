package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.execerrors.MissingReturnError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;

public class MethodBody extends AbstractMethodBody {

    final ListDeclVar variables;
    final ListInst insts;

    public MethodBody(ListDeclVar ldv, ListInst li) {
        this.variables = ldv;
        this.insts = li;
    }

    @Override
    public void verifyMethodBody(DecacCompiler compiler, EnvironmentExp envExpParams,
            ClassDefinition currentClass, Type returnType) throws ContextualError {

        variables.verifyListDeclVariable(compiler, envExpParams, currentClass);
        insts.verifyListInst(compiler, envExpParams, currentClass, returnType);
    }

    @Override
    public void codeGenMethodBody(DecacCompiler compiler, ClassDefinition currentClass, Label finLabel) {
        variables.codeGenListDeclVar(compiler, currentClass);
        insts.codeGenListInst(compiler, finLabel);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        variables.prettyPrint(s, prefix, true);
        insts.prettyPrint(s, prefix, false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        variables.iter(f);
        insts.iter(f);
    }

    public void decompile(IndentPrintStream s) {
        s.println("{");
        s.indent();
        s.indent(); // TODO: trouver un autre moyen j'aime pas le double indent
        variables.decompile(s);
        insts.decompile(s);
        s.unindent();
        s.println("}");
    }

    @Override
    public void codeGenByteMethodBody(MethodVisitor mv, DecacCompiler compiler, Type returnType) throws ContextualError {
        // 1) Generate bytecode for declared variables
        variables.codeGenListDeclVarByte(mv, compiler);
        // 2) Generate bytecode for instructions
        insts.codeGenListInstByte(mv, compiler);
    
     
        if (!returnType.isVoid()) {
            if (returnType.isInt() || returnType.isBoolean()) {
                mv.visitInsn(Opcodes.ICONST_0);   // push default 0
                mv.visitInsn(Opcodes.IRETURN);    // then IRETURN
            } else if (returnType.isFloat()) {
                mv.visitInsn(Opcodes.FCONST_0);
                mv.visitInsn(Opcodes.FRETURN);
            } else if (returnType.isClass()) {
                mv.visitInsn(Opcodes.ACONST_NULL);
                mv.visitInsn(Opcodes.ARETURN);
            }
        } else {
            // For void methods, if the user didn't produce a RETURN, do it automatically:
            mv.visitInsn(Opcodes.RETURN);
        }
    }
    
    

}
