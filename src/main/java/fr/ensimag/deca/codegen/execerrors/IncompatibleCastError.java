package fr.ensimag.deca.codegen.execerrors;

import fr.ensimag.ima.pseudocode.Label;

public class IncompatibleCastError extends ExecError {

    public static final IncompatibleCastError INSTANCE = new IncompatibleCastError();
    
    public IncompatibleCastError() {
        super(new Label("incompatible_cast_error"), "Error: Incompatible cast");
    }

    @Override
    public boolean equals(Object o) {
        return o == this;
    }

}
