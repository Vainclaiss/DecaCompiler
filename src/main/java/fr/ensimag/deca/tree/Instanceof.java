package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;

public class Instanceof extends AbstractOpIneq {
    public Instanceof(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "instanceof";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        s.print(getLeftOperand().decompile());
        s.print(" instanceof ");
        s.print(getRightOperand().decompile());
        s.print(")");
    }

    @Override
    protected int getJumpOpcodeForInt() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getJumpOpcodeForInt'");
    }

    @Override
    protected int getJumpOpcodeForFloat() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getJumpOpcodeForFloat'");
    }

}
