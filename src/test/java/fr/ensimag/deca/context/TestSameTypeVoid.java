package fr.ensimag.deca.context;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestSameTypeVoid {
    final Type VOID = new VoidType(null);
    final Type FLOAT = new FloatType(null);
    final Type INT = new IntType(null);
    final Type BOOLEAN = new BooleanType(null);
    final Type STRING = new StringType(null);

    @Test
    public void testSameTypeVoidAndFloat() {
        assertFalse(VOID.sameType(FLOAT));
    }

    @Test
    public void testSameTypeVoidAndInt() {
        assertFalse(VOID.sameType(INT));
    }

    @Test
    public void testSameTypeVoidAndBoolean() {
        assertFalse(VOID.sameType(BOOLEAN));
    }

    @Test
    public void testSameTypeVoidAndString() {
        assertFalse(VOID.sameType(STRING));
    }

    @Test
    public void testSameTypeVoidAndVoid() {
        assertTrue(VOID.sameType(VOID));
    }
}