package fr.ensimag.deca.context;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestNullType {
    final Type VOID = new VoidType(null);
    final Type FLOAT = new FloatType(null);
    final Type INT = new IntType(null);
    final Type BOOLEAN = new BooleanType(null);
    final Type STRING = new StringType(null);
    final Type NULL = new NullType(null);

    @Test
    public void testNullIsNull() {
        assertTrue(NULL.isNull());
        assertTrue(NULL.isClassOrNull());

    }

    @Test
    public void testSameTypeNullAndVoid() {
        assertFalse(NULL.sameType(VOID));
    }

    @Test
    public void testSameTypeNullAndFloat() {
        assertFalse(NULL.sameType(FLOAT));
    }

    @Test
    public void testSameTypeNullAndInt() {
        assertFalse(NULL.sameType(INT));
    }

    @Test
    public void testSameTypeNullAndBoolean() {
        assertFalse(NULL.sameType(BOOLEAN));
    }

    @Test
    public void testSameTypeNullAndString() {
        assertFalse(NULL.sameType(STRING));
    }

    @Test
    public void testSameTypeNullAndNull() {
        assertTrue(NULL.sameType(NULL));
    }
}