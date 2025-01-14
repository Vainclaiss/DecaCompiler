package fr.ensimag.deca.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;

// A FAIRE: étendre cette classe pour traiter la partie "avec objet" de Déca
/**
 * Environment containing types. Initially contains predefined identifiers, more
 * classes can be added with declareClass().
 *
 * @author gl01
 * @date 01/01/2025
 */
public class EnvironmentType {
    public EnvironmentType(DecacCompiler compiler) {
        
        envTypes = new HashMap<Symbol, TypeDefinition>();
        
        Symbol intSymb = compiler.createSymbol("int");
        INT = new IntType(intSymb);
        envTypes.put(intSymb, new TypeDefinition(INT, Location.BUILTIN));

        Symbol floatSymb = compiler.createSymbol("float");
        FLOAT = new FloatType(floatSymb);
        envTypes.put(floatSymb, new TypeDefinition(FLOAT, Location.BUILTIN));

        Symbol voidSymb = compiler.createSymbol("void");
        VOID = new VoidType(voidSymb);
        envTypes.put(voidSymb, new TypeDefinition(VOID, Location.BUILTIN));

        Symbol booleanSymb = compiler.createSymbol("boolean");
        BOOLEAN = new BooleanType(booleanSymb);
        envTypes.put(booleanSymb, new TypeDefinition(BOOLEAN, Location.BUILTIN));

        Symbol stringSymb = compiler.createSymbol("string");
        STRING = new StringType(stringSymb);
        // not added to envTypes, it's not visible for the user.

        // creation of Object
        Symbol objectSymb = compiler.createSymbol("Object");
        OBJECT = new ClassType(objectSymb, Location.BUILTIN, null);

        // creation of the equals method
        Signature equalsSignature = new Signature();
        equalsSignature.add(OBJECT);
        MethodDefinition equals = new MethodDefinition(BOOLEAN, Location.BUILTIN, equalsSignature, 0);

        try {
            OBJECT.getDefinition().getMembers().declare(compiler.createSymbol("equals"), equals);
            OBJECT.getDefinition().incNumberOfMethods();
        } catch (DoubleDefException e) {
            // impossible case
        }
        envTypes.put(objectSymb, OBJECT.getDefinition());

        Symbol nullSymb = compiler.createSymbol("null");
        NULL = new NullType(nullSymb);
        // not added to envTypes, it's impossible to declare a variable of type null.

    }

    private final Map<Symbol, TypeDefinition> envTypes;

    /**
     * @return the envTypes Map, read-only.
     */
    public Map<Symbol, TypeDefinition> getEnvTypes() {
        return Collections.unmodifiableMap(envTypes);
    }

    public TypeDefinition defOfType(Symbol s) {
        return envTypes.get(s);
    }

    public void addType(Symbol s, TypeDefinition typeDef) {
        envTypes.put(s, typeDef);
    }

    public final VoidType    VOID;
    public final IntType     INT;
    public final FloatType   FLOAT;
    public final StringType  STRING;
    public final BooleanType BOOLEAN;
    public final NullType NULL;
    public final ClassType OBJECT;
}
