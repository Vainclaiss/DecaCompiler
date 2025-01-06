package fr.ensimag.ima.pseudocode;

import static org.mockito.ArgumentMatchers.intThat;

import fr.ensimag.deca.CompilerOptions;

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
    // TODO : find a better way to retreive the number of register to use
    public static final int RMAX = CompilerOptions.getNumRegisters() - 1;
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
     * To manage if a register can be use or not
     */
    private static final boolean[] registreLibre = new boolean[16];
    /**
     * General Purpose Registers. Array is private because Java arrays cannot be
     * made immutable, use getR(i) to access it.
     */
    private static final GPRegister[] R = initRegisters();

    /**
     * Return the index of a usable register >= 2
     * Return -1 if there is no free registers
     * 
     * @return
     */
    public static int getIndexRegistreLibre() {
        for (int i = 2; i < registreLibre.length; i++) {
            if (registreLibre[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * set the state of registreLibre[indexRegister at estLibre]
     * 
     * @param indexRegister
     * @param estLibre
     */
    public static void setRegistreLibre(int indexRegister, boolean estLibre) {
        registreLibre[indexRegister] = estLibre;
    }

    /**
     * General Purpose Registers
     */
    public static GPRegister getR(int i) {
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
            registreLibre[i] = true;
        }
        return res;
    }
}
