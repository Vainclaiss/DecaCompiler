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
        return typeToJVMDescriptor(this);
    }
    
    public static String typeToJVMDescriptor(Type t) {
        if (t.isInt()) {
            return "I";
        } else if (t.isFloat()) {
            return "F";
        } else if (t.isBoolean()) {
            return "Z";
        } else if (t.isClass()) {
            // If it's the Deca builtin "Object", map it to java/lang/Object
            String decaName = t.getName().getName(); 
            if (decaName.equals("Object")) {
                return "Ljava/lang/Object;";
            } else {
                // For normal user classes, e.g. "mypackage.MyClass"
                // => "Lmypackage/MyClass;"
                String internalName = decaName.replace('.', '/');
                return "L" + internalName + ";";
            }
        } else if (t.isVoid()) {
            return "V";
        }
        throw new UnsupportedOperationException("Unsupported Deca Type for JVM descriptor: " + t);
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
