package fr.ensimag.deca.context;

import fr.ensimag.deca.tree.Location;

/**
 * Definition of a variable.
 *
 * @author gl01
 * @date 01/01/2025
 */
public class VariableDefinition extends ExpDefinition {

    private int localIndex = -1;

    public VariableDefinition(Type type, Location location) {
        super(type, location);
    }

    @Override
    public String getNature() {
        return "variable";
    }

    @Override
    public boolean isExpression() {
        return true;
    }

    public void setLocalIndex(int index) {
        this.localIndex = index;
    }

    public int getLocalIndex() {
        return this.localIndex;
    }
}
