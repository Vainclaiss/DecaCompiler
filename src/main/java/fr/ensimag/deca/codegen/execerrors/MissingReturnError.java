package fr.ensimag.deca.codegen.execerrors;

import fr.ensimag.ima.pseudocode.Label;

public class MissingReturnError extends ExecError {
    
    public static final MissingReturnError INSTANCE = new MissingReturnError();

    public MissingReturnError() {
        super(new Label("missing_return_error"), "Error: return instruction missing");
    }

    @Override
    public boolean equals(Object o) {
        return o == this;
    }

}
