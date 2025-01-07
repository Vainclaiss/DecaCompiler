package fr.ensimag.deca.codegen.execerrors;

import fr.ensimag.ima.pseudocode.Label;

public class IOError extends ExecError {

    public static final IOError INSTANCE = new IOError();

    private IOError() {
        super(new Label("io_error"), "Error: Input/Output error");
    }

    @Override
    public boolean equals(Object o) {
        return o == this;
    }
}
