package fr.ensimag.deca.codegen.execerrors;

import fr.ensimag.ima.pseudocode.Label;

public class StackOverflowError extends ExecError {

    public static final StackOverflowError INSTANCE = new StackOverflowError();

    public StackOverflowError() {
        super(new Label("stack_overflow_error"), "Error: Stack Overflow");
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof StackOverflowError;
    }

}
