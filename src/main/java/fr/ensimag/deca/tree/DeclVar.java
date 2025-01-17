package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.RegisterOffset;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.objectweb.asm.MethodVisitor;

/**
 * @author gl01
 * @date 01/01/2025
 */
public class DeclVar extends AbstractDeclVar {

    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;
    final private AbstractInitialization initialization;

    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }

    public AbstractIdentifier getVarName() {
        return varName;
    }

    public AbstractInitialization getInitialization() {
        return initialization;
    }

    @Override
    protected void verifyDeclVar(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {

        Type realType = type.verifyType(compiler);
        if (realType.isVoid())
            throw new ContextualError("Error: 'void' cannot be used as a type for variable declaration", getLocation());

        VariableDefinition varDef = new VariableDefinition(realType, varName.getLocation());
        varName.setDefinition(varDef);
        try {
            localEnv.declare(varName.getName(), varDef);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Error: Multiple declaration of '" + varName.getName().toString()
                    + "' , first declaration at " + localEnv.get(varName.getName()).getLocation(),
                    varName.getLocation());
        }

        initialization.verifyInitialization(compiler, realType, localEnv, currentClass);
    }

    @Override
    protected void codeGenDeclVar(DecacCompiler compiler, DAddr adresse) {
        varName.getVariableDefinition().setOperand(adresse);
        initialization.codeGenInitialization(compiler, adresse);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(type.decompile() + " " + varName.decompile() + initialization.decompile() + ";");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }



    protected void codeGenByteDeclVar(MethodVisitor mv, DecacCompiler compiler) {
        int localIndex = compiler.allocateLocalIndex();
        System.out.println(localIndex);
        varName.getVariableDefinition().setLocalIndex(localIndex);

        initialization.codeGenByteInitialization(mv, localIndex,compiler);
    }
    



}
