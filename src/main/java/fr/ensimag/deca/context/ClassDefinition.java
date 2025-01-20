package fr.ensimag.deca.context;

import java.rmi.registry.Registry;
import java.util.Map;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

/**
 * Definition of a class.
 *
 * @author gl01
 * @date 01/01/2025
 */
public class ClassDefinition extends TypeDefinition {


    public void setNumberOfFields(int numberOfFields) {
        this.numberOfFields = numberOfFields;
    }

    public int getNumberOfFields() {
        return numberOfFields;
    }

    public void incNumberOfFields() {
        this.numberOfFields++;
    }

    public int getNumberOfMethods() {
        return numberOfMethods;
    }

    public void setNumberOfMethods(int n) {
        Validate.isTrue(n >= 0);
        numberOfMethods = n;
    }
    
    public int incNumberOfMethods() {
        numberOfMethods++;
        return numberOfMethods;
    }

    private int numberOfFields = 0;
    private int numberOfMethods = 0;
    
    @Override
    public boolean isClass() {
        return true;
    }
    
    @Override
    public ClassType getType() {
        // Cast succeeds by construction because the type has been correctly set
        // in the constructor.
        return (ClassType) super.getType();
    }

    public ClassDefinition getSuperClass() {
        return superClass;
    }

    public void setOperand(DVal operand) {
        this.operand = operand;
    }

    public DAddr getDAddrOperand() {
        return (DAddr)operand;
    }

    public DVal getDValOperand() {
        return operand;
    }

    // adresse de la classe i.e adresse du début de la vtable pour cette classe
    private DVal operand;

    private final EnvironmentExp members;
    private final ClassDefinition superClass;
    private Label[] vTable;

    /**
     * 
     * @return a copie of vTable or null if vTable is null
     */
    public Label[] getVtable() {
        return vTable == null ? null : vTable.clone();
    }

    public void printVtable() {
        System.out.println("vtable of " + getType().toString());
        for (int index = 1; index <= numberOfMethods; index++) {
            System.out.println("Index: " + index + ", methodLabel: " + vTable[index-1].toString());
        }
    }

    public void completeVtable() {
        this.vTable = new Label[numberOfMethods];

        if (superClass != null) {
            superClass.completeVtable();
            Label[] superVtable = superClass.getVtable();
            for (int i = 0; i < superClass.getNumberOfMethods(); i++) {
                this.vTable[i] = superVtable[i];
            }
        }
        for (Map.Entry<Symbol, ExpDefinition> methodEntry : members.getCurrEnv().entrySet()) {
            Symbol methodSymbol = methodEntry.getKey();
            ExpDefinition expDef = methodEntry.getValue();
            if (expDef.isMethod()) {
                MethodDefinition methodDef = (MethodDefinition) expDef;   // the cast succeed because of the check

                Label methodLabel = new Label("code." + getType().getName().toString() + "." + methodSymbol.toString());

                methodDef.setLabel(methodLabel);
                this.vTable[methodDef.getIndex()-1] = methodLabel;
            }
        }
    }
    public String getInternalName() {
        return getType().getName().toString().replace('.', '/');
    }
    

    /**
     * 
     * @param compiler
     * @param offset
     * @return
     */
    public void codeGenVtable(DecacCompiler compiler) {

        compiler.addComment("Code de la table des méthodes de " + getType().getName().toString());
        if (superClass != null) {
            compiler.addInstruction(new LEA(superClass.getDAddrOperand(), Register.R0));
        }
        else {
            compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));
        }

        setOperand(new RegisterOffset(compiler.incrGBOffset(), Register.GB));
        compiler.addInstruction(new STORE(Register.R0, (DAddr)operand));

        for (Label label : vTable) {
            compiler.addInstruction(new LOAD(new LabelOperand(label), Register.R0));
            compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(compiler.incrGBOffset(), Register.GB)));
        }
        compiler.getStackOverflowCounter().addVariables(numberOfMethods+1);
    }

    public EnvironmentExp getMembers() {
        return members;
    }

    public ClassDefinition(ClassType type, Location location, ClassDefinition superClass) {
        super(type, location);
        EnvironmentExp parent;
        if (superClass != null) {
            parent = superClass.getMembers();
        } else {
            parent = null;
        }
        members = new EnvironmentExp(parent);
        this.superClass = superClass;
    }
    
}
