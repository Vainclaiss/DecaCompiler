package fr.ensimag.ima.pseudocode;

import static org.mockito.ArgumentMatchers.intThat;

import fr.ensimag.deca.CompilerOptions;
import fr.ensimag.deca.DecacCompiler;

/**
 * Register operand (including special registers like SP).
 * 
 * @author Ensimag
 * @date 01/01/2025
 */
public class Register extends DVal {
    private String name;

    protected Register(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Max register number
     */
    public static int RMAX = 15;
    /**
     * Global Base register
     */
    public static final Register GB = new Register("GB");
    /**
     * Local Base register
     */
    public static final Register LB = new Register("LB");
    /**
     * Stack Pointer
     */
    public static final Register SP = new Register("SP");

    /**
     * General Purpose Registers. Array is private because Java arrays cannot be
     * made immutable, use getR(i) to access it.
     */
    private static final GPRegister[] R = initRegisters();

    /**
     * General Purpose Registers
     */
    public static GPRegister getR(int i) {
        return R[i];
    }

    /**
     * General Purpose Registers
     */
    public static GPRegister getR(DecacCompiler compiler, int i) {
        compiler.getStackOverflowCounter().refreshMaxRegister(i);
        return R[i];
    }

    /**
     * Convenience shortcut for R[0]
     */
    public static final GPRegister R0 = R[0];
    /**
     * Convenience shortcut for R[1]
     */
    public static final GPRegister R1 = R[1];

    private static GPRegister[] initRegisters() {
        GPRegister[] res = new GPRegister[16];
        for (int i = 0; i < 16; i++) {
            res[i] = new GPRegister("R" + i, i);
        }
        return res;
    }
}
