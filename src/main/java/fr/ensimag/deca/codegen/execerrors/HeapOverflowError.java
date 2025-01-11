package fr.ensimag.deca.codegen.execerrors;

import fr.ensimag.ima.pseudocode.Label;

public class HeapOverflowError extends ExecError {

    public static final HeapOverflowError INSTANCE = new HeapOverflowError();

    public HeapOverflowError() {
        super(new Label("heap_overflow_error"), "Error: Heap Overflow");
    }

    @Override
    public boolean equals(Object o) {
        return o == this;
    }
    
}