package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.VoidType;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * @author gl01
 * @date 01/01/2025
 */
public class Main extends AbstractMain {
    private static final Logger LOG = Logger.getLogger(Main.class);

    private ListDeclVar declVariables;
    private ListInst insts;

    public Main(ListDeclVar declVariables,
            ListInst insts) {
        Validate.notNull(declVariables);
        Validate.notNull(insts);
        this.declVariables = declVariables;
        this.insts = insts;
    }

    public ListDeclVar getDeclVariables() {
        return this.declVariables;
    }

    public ListInst getInsts() {
        return this.insts;
    }

    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify Main: start");

        // On est dans le main bloc donc null
        EnvironmentExp mainEnv = new EnvironmentExp(null);
        Symbol voidSymb = compiler.createSymbol("void");
        VoidType voidType = new VoidType(voidSymb);

        LOG.debug("Verifying list of declared variables");
        declVariables.verifyListDeclVariable(compiler, mainEnv, null);
        LOG.debug("List of declared variables verified");

        LOG.debug("Verifying list of instructions");
        insts.verifyListInst(compiler, mainEnv, null, voidType);
        LOG.debug("List of instructions verified");

        LOG.debug("verify Main: end");
    }

    @Override
    protected void codeGenMain(DecacCompiler compiler) {
        // TODO: rajouter la table des methodes.
        declVariables.codeGenListDeclVar(compiler, null);

        compiler.addComment("Beginning of main instructions:");
        insts.codeGenListInst(compiler);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.println("{");
        s.indent();
        declVariables.decompile(s);
        insts.decompile(s);
        s.unindent();
        s.println("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        declVariables.iter(f);
        insts.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        declVariables.prettyPrint(s, prefix, false);
        insts.prettyPrint(s, prefix, true);
    }


    @Override
    protected void codeGenByteMain(MethodVisitor mv,DecacCompiler compiler) throws ContextualError {
        declVariables.codeGenListDeclVarByte(mv, compiler); // On genere le bytecode pour les variables
        
            insts.codeGenListInstByte(mv,compiler); // On genere le bytecode pour toute les instructions
            System.out.println("Bytecode generation for main method completed.");
        
    }
    
}
