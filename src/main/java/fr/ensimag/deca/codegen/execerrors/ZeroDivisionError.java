package fr.ensimag.deca.codegen.execerrors;

import fr.ensimag.ima.pseudocode.Label;

public class ZeroDivisionError extends ExecError{

    public static final ZeroDivisionError INSTANCE = new ZeroDivisionError();
    
    public ZeroDivisionError() {
        super(new Label("zero_division_error"), "Error: division by 0");
    }

    @Override
    public boolean equals(Object o) {
        return o == this;
    }

}
