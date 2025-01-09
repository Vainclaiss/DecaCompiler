package fr.ensimag.deca.context;

import java.util.ArrayList;
import java.util.List;

/**
 * Signature of a method (i.e. list of arguments)
 *
 * @author gl01
 * @date 01/01/2025
 */
public class Signature {
    List<Type> args = new ArrayList<Type>();

    public void add(Type t) {
        args.add(t);
    }
    
    public Type paramNumber(int n) {
        return args.get(n);
    }
    
    public int size() {
        return args.size();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Signature)) {
            return false;
        }
        Signature otherSignature = (Signature) other;

        return args.equals(otherSignature.args);
    }

    @Override
    public int hashCode() {
        return args.hashCode();
    }

}
