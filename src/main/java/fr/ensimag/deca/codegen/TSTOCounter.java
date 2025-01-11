package fr.ensimag.deca.codegen;

public class TSTOCounter {
    private int temporaryOnStack;
    private int paramsOnStack;
    private int variables;
    private int savedRegisters;

    private int maxTSTO;
    private int maxTemporaryOnStack;
    private int maxParamsOnStack;
    private int maxVariables;
    private int maxSavedRegisters;

    public TSTOCounter() {
        this.temporaryOnStack = 0;
        this.paramsOnStack = 0;
        this.variables = 0;
        this.savedRegisters = 0;
        
        this.maxTSTO = 0;
        this.maxTemporaryOnStack = 0;
        this.maxParamsOnStack = 0;
        this.maxVariables = 0;
        this.maxSavedRegisters = 0;
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
        savedRegisters += i;
        updateMaxTSTO();
    }

    private int sumTSTO() {
        return temporaryOnStack + paramsOnStack + variables + savedRegisters;
    }

    private void updateMaxTSTO() {
        int s = sumTSTO();
        if (s >  maxTSTO) {
            maxTSTO = s;
            maxTemporaryOnStack = temporaryOnStack;
            maxParamsOnStack = paramsOnStack;
            maxVariables = variables;
            maxSavedRegisters = savedRegisters;
        }
    }

    public int getMaxTSTO() {
        return maxTSTO;
    }

    public String getDetailsMaxTSTO() {
        return maxTemporaryOnStack + " (temporary) + " + maxParamsOnStack + " (parameters) + " + maxVariables + " (variables) + " + maxSavedRegisters + " (registers)";
    }
}
