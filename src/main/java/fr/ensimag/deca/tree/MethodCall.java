package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.execerrors.IOError;
import fr.ensimag.deca.codegen.execerrors.NullDereference;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.RFLOAT;
import fr.ensimag.ima.pseudocode.instructions.RINT;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.SUB;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;

import java.beans.MethodDescriptor;
import java.io.PrintStream;
import java.util.List;

public class MethodCall extends AbstractExpr {

    private final AbstractExpr leftOperand;
    private final AbstractIdentifier methodName;
    private final ListExpr rightOperand;

    public MethodCall(AbstractExpr leftOperand, AbstractIdentifier methodName, ListExpr rightOperand) {
        this.leftOperand = leftOperand;
        this.methodName = methodName;
        this.rightOperand = rightOperand;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.debug("Verifying left operand expression");
        Type classType = leftOperand.verifyExpr(compiler, localEnv, currentClass);
        if (!classType.isClass()) {
            LOG.error("Left operand of a selection is not a class");
            throw new ContextualError("Error: Left operand of a selection must be a class", getLocation());
        }

        LOG.debug("Verifying class type");
        ClassDefinition class2 = classType.asClassType("Error: Cast failed from Type to ClassType", getLocation())
                .getDefinition();
        EnvironmentExp envExp2 = class2.getMembers();
        MethodDefinition methodDef = methodName.verifyMethod(envExp2);

        LOG.debug("Verifying method arguments");
        rightOperand.verifyRValueStar(compiler, localEnv, currentClass, methodDef.getSignature());

        LOG.debug("Setting return type of the method");
        setType(methodDef.getType());
        return methodDef.getType();
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        // TODO : gerer le cas de this
        String methodeLabel = methodName.getMethodDefinition().getLabel().toString().replace("code.", "");
        compiler.addComment("Empilement des arguments de " + methodeLabel);
        compiler.addInstruction(new ADDSP(rightOperand.size() + 1));
        // TODO : c'est frauduleux !!! il faut gerer tout type d'exp
        leftOperand.codeExp(compiler, n); // method call, new, selection, variables in R0
        compiler.addInstruction(new STORE(Register.getR(n), new RegisterOffset(0, Register.SP)));

        List<AbstractExpr> params = rightOperand.getList();
        compiler.getStackOverflowCounter().addParamsOnStack(params.size());
        for (int i = params.size(); i > 0; i--) {
            params.get(i - 1).codeExp(compiler, n); // TODO : checker le numero de registre
            compiler.addInstruction(new STORE(Register.getR(compiler, n), new RegisterOffset(-i, Register.SP)));
        }

        compiler.addComment("Appel de la methode " + methodeLabel);
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.SP), Register.R0));

        if (!compiler.getCompilerOptions().getSkipExecErrors()) {
            compiler.addExecError(NullDereference.INSTANCE);
            compiler.addInstruction(new CMP(new NullOperand(), Register.R0));
            compiler.addInstruction(new BEQ(NullDereference.INSTANCE.getLabel()));
        }

        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.R0), Register.R0));
        compiler.addInstruction(new BSR(new RegisterOffset(methodName.getMethodDefinition().getIndex(), Register.R0)));
        compiler.addInstruction(new SUBSP(rightOperand.size() + 1));
        compiler.addComment("fin appel de methode");
        compiler.addInstruction(new LOAD(Register.R0, Register.getR(compiler, n)));
    }

    @Override
    public DVal getDVal() {
        return Register.R0;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        codeExp(compiler);
    }

    @Override
    protected void codeExp(DecacCompiler compiler) {
        codeExp(compiler, 2); // TODO : vraiment pas optimiser mais sinon possible erreur
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, boolean branchIfTrue, Label e) {
        codeExp(compiler, 2);
        // TODO : peut etre metre R2 au lieu de R0 pour plus de securité
        compiler.addInstruction(new CMP(1, Register.R0));
        if (branchIfTrue) {
            compiler.addInstruction(new BEQ(e));
        } else {
            compiler.addInstruction(new BNE(e));
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        leftOperand.decompile(s);
        s.print(".");
        methodName.decompile(s);
        s.print("(");
        rightOperand.decompile(s);
        s.print(")");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        leftOperand.prettyPrint(s, prefix, false);
        methodName.prettyPrint(s, prefix, false);
        rightOperand.prettyPrint(s, prefix, false);
    }

}
