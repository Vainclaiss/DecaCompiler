package fr.ensimag.deca.codegen.execerrors;

import java.util.Objects;

import fr.ensimag.ima.pseudocode.Label;

public abstract class ExecError {

    protected ExecError(Label label, String errorMsg) {
        this.label = label;
        this.errorMsg = errorMsg;
    }

    protected Label label;
    protected String errorMsg;
    
    public Label getLabel() {
        return label;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, errorMsg);
    }

}
