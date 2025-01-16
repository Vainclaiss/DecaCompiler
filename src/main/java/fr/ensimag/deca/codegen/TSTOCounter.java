package fr.ensimag.deca.codegen;

public class TSTOCounter {
    private int temporaryOnStack;
    private int paramsOnStack;
    private int variables;

    private int maxTSTO;
    private int maxTemporaryOnStack;
    private int maxParamsOnStack;
    private int maxVariables;
    private int maxSavedRegisters;

    private int maxRegisterUsed;

    public TSTOCounter() {
        this.temporaryOnStack = 0;
        this.paramsOnStack = 0;
        this.variables = 0;
        
        this.maxTSTO = 0;
        this.maxTemporaryOnStack = 0;
        this.maxParamsOnStack = 0;
        this.maxVariables = 0;

        this.maxRegisterUsed = 0;
    }

    public void refreshMaxRegister(int i) {
        if (i > maxRegisterUsed) {
            maxRegisterUsed = i;
        }
    }

    public void addParamsOnStack(int i) {
        paramsOnStack += i;
        updateMaxTSTO();
    }

    public void addTemporaryOnStack(int i) {
        temporaryOnStack += i;
        updateMaxTSTO();
    }

    public void addVariables(int i) {
        variables += i;
        updateMaxTSTO();
    }

    public void addSavedRegisters(int i) {
        maxSavedRegisters += i;
        updateMaxTSTO();
    }

    private int sumTSTO() {
        return temporaryOnStack + paramsOnStack + variables + maxSavedRegisters;
    }

    private void updateMaxTSTO() {
        int s = sumTSTO();
        if (s >  maxTSTO) {
            maxTSTO = s;
            maxTemporaryOnStack = temporaryOnStack;
            maxParamsOnStack = paramsOnStack;
            maxVariables = variables;
        }
    }

    public int getMaxTSTO() {
        return maxTSTO;
    }

    public int getMaxRegisterUsed() {
        return maxRegisterUsed;
    }

    public String getDetailsMaxTSTO() {
        return maxTemporaryOnStack + " (temporary) + " + maxParamsOnStack + " (parameters) + " + maxVariables + " (variables) + " + maxSavedRegisters + " (registers)";
    }
}
