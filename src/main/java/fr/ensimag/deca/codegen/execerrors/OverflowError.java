package fr.ensimag.deca.codegen.execerrors;

import fr.ensimag.ima.pseudocode.Label;

public class OverflowError extends ExecError {

    public static final OverflowError INSTANCE = new OverflowError();

    public OverflowError() {
        super(new Label("overflow_error"), "Error: Overflow during arithmetic operation");
    }

    @Override
    public boolean equals(Object o) {
        return o == this;
    }

}
