package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;

public class Instanceof extends AbstractOpIneq{
      public Instanceof(AbstractExpr leftOperand, AbstractExpr rightOperand) {
            super(leftOperand, rightOperand);
        }
        @Override
        protected String getOperatorName() {
            return "instanceof";
        }

}
