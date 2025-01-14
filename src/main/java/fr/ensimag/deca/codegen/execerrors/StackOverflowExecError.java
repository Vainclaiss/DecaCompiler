package fr.ensimag.deca.codegen.execerrors;

import fr.ensimag.ima.pseudocode.Label;

public class StackOverflowExecError extends ExecError {

    public static final StackOverflowExecError INSTANCE = new StackOverflowExecError();

    public StackOverflowExecError() {
        super(new Label("stack_overflow_error"), "Error: Stack Overflow");
    }
}
