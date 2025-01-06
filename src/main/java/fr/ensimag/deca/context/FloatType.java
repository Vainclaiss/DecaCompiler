package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.NullaryInstruction;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WINT;

/**
 *
 * @author Ensimag
 * @date 01/01/2025
 */
public class FloatType extends Type {

    public FloatType(SymbolTable.Symbol name) {
        super(name);
    }

    @Override
    public boolean isFloat() {
        return true;
    }

    @Override
    public boolean sameType(Type otherType) {
        return otherType.isFloat();
    }

}
