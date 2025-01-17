package fr.ensimag.deca.codegen.execerrors;

import fr.ensimag.ima.pseudocode.Label;

public class NullDereference extends ExecError {

    public static final NullDereference INSTANCE = new NullDereference();

    public NullDereference() {
        super(new Label("null_dereference_error"), "Error: Null dereference");
    }
}
