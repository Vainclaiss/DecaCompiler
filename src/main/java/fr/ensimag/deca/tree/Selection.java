package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.execerrors.HeapOverflowError;
import fr.ensimag.deca.codegen.execerrors.IOError;
import fr.ensimag.deca.codegen.execerrors.NullDereference;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.RFLOAT;
import fr.ensimag.ima.pseudocode.instructions.RINT;

import java.io.PrintStream;
import java.lang.reflect.Field;

public class Selection extends AbstractLValue {

    final private AbstractExpr leftOperand;
    final private AbstractIdentifier rightOperand;

    public Selection(AbstractExpr leftOperand, AbstractIdentifier rightOperand) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    @Override
    public DVal getDVal() {
        return new RegisterOffset(rightOperand.getFieldDefinition().getIndex(), Register.R0);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        LOG.debug("verify Selection: start");
        Type classType = leftOperand.verifyExpr(compiler, localEnv, currentClass);
        if (!classType.isClass()) {
            throw new ContextualError("Error: Left operand of a selection must be a class", getLocation());
        }

        ClassDefinition class2 = classType.asClassType("Error: Cast failed from Type to ClassType", getLocation())
                .getDefinition();
        EnvironmentExp envExp2 = class2.getMembers();
        FieldDefinition fieldDef = rightOperand.verifyField(envExp2);
        ClassDefinition classField = fieldDef.getContainingClass();

        if ((fieldDef.getVisibility() == Visibility.PROTECTED) && ( currentClass == null ||
                (!(class2.getType().isSubClassOf(currentClass.getType()) && currentClass.getType().isSubClassOf(classField.getType()))))) {

            throw new ContextualError(
                    "Error: Unauthorized access to the protected field '" + rightOperand.getName().toString() + "'",
                    getLocation());
        }

        setType(fieldDef.getType());

        LOG.debug("verify Selection: end");
        return fieldDef.getType();
    }

    @Override
    protected void codeExp(DecacCompiler compiler) {

        leftOperand.codeExp(compiler, 0); // peut faire 2 fois la meme inst -> pas grave
        compiler.addInstruction(new LOAD(leftOperand.getDVal(), Register.getR(compiler, 0)));

        if (!compiler.getCompilerOptions().getSkipExecErrors()) {
            compiler.addExecError(NullDereference.INSTANCE);
            compiler.addInstruction(new CMP(new NullOperand(), Register.getR(compiler, 0)));
            compiler.addInstruction(new BEQ(NullDereference.INSTANCE.getLabel()));
        }
    }

    @Override
    protected void codeExp(DecacCompiler compiler, int n) {
        // TODO: gerer le cas de this
        codeExp(compiler);
        compiler.addInstruction(
                new LOAD(new RegisterOffset(rightOperand.getFieldDefinition().getIndex(), Register.getR(compiler, 0)),
                        Register.getR(compiler, n)));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(leftOperand.decompile() + "." + rightOperand.decompile());
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        leftOperand.iter(f);
        rightOperand.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        leftOperand.prettyPrint(s, prefix, false);
        rightOperand.prettyPrint(s, prefix, false);
    }

}