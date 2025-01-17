package fr.ensimag.deca.context;

import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.ima.pseudocode.NullaryInstruction;

/**
 * Deca Type (internal representation of the compiler)
 *
 * @author gl01
 * @date 01/01/2025
 */

public abstract class Type {


    /**
     * True if this and otherType represent the same type (in the case of
     * classes, this means they represent the same class).
     */
    public abstract boolean sameType(Type otherType);

    private final Symbol name;

    public Type(Symbol name) {
        this.name = name;
    }

    public Symbol getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName().toString();
    }

    public boolean isClass() {
        return false;
    }

    public boolean isInt() {
        return false;
    }

    public boolean isFloat() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public boolean isVoid() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    public boolean isNull() {
        return false;
    }

    public boolean isClassOrNull() {
        return false;
    }


        public String toJVMDescriptor() {
            if (this.isInt()) {
                return "I";
            } else if (this.isFloat()) {
                return "F";
            } else if (this.isBoolean()) {
                return "Z";
            } else if (this.isClass()) {
                String internalName = this.getName().toString().replace('.', '/');
                return "L" + internalName + ";";
            } else if (this.isVoid()) {
                return "V";
            }
            throw new UnsupportedOperationException("Unsupported Deca Type for JVM descriptor: " + this);
        }
    
    

    /**
     * Returns the same object, as type ClassType, if possible. Throws
     * ContextualError(errorMessage, l) otherwise.
     *
     * Can be seen as a cast, but throws an explicit contextual error when the
     * cast fails.
     */
    public ClassType asClassType(String errorMessage, Location l)
            throws ContextualError {
        throw new ContextualError(errorMessage, l);
    }

    public boolean subType(Type otherType) {
        if ((this.isNull() && otherType.isClass()) || this.sameType(otherType)) {
            return true;
        }

        if (this.isClass() && otherType.isClass()) {
            ClassType thisType = (ClassType) this;
            ClassType thatType = (ClassType) otherType;
            return thisType.isSubClassOf(thatType);
        }

        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Type)) {
            return false;
        }
        return this.sameType((Type)other);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
