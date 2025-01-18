package fr.ensimag.deca.context;

import fr.ensimag.deca.tree.Location;

/**
 * Definition of a method parameter.
 *
 * @author gl01
 * @date 01/01/2025
 */
public class ParamDefinition extends ExpDefinition {
    private int localIndex;

    public ParamDefinition(Type type, Location location) {
        super(type, location);
    }

    @Override
    public String getNature() {
        return "parameter";
    }

    @Override
    public boolean isExpression() {
        return true;
    }

    @Override
    public boolean isParam() {
        return true;
    }

     
    public int getIndexInLocalTable() { return localIndex; }
    public void setIndexInLocalTable(int i) { localIndex = i; }

}
