package fr.ensimag.deca.codegen.execerrors;

import fr.ensimag.ima.pseudocode.Label;

public class MissingReturnError extends ExecError {
    
    public MissingReturnError(String methodName) {
        super(new Label("missing_return_error_" + methodName), "Error: return instruction missing in " + methodName);
    }
}
