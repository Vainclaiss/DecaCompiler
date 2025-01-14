package fr.ensimag.deca.context;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestClassType {
    final Type VOID = new VoidType(null);
    final Type FLOAT = new FloatType(null);
    final Type INT = new IntType(null);
    final Type BOOLEAN = new BooleanType(null);
    final Type CLASS = new ClassType(null);

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
    public void testSameTypeVoidAndClass() {
        assertFalse(VOID.sameType(CLASS));
    }

    @Test
    public void testSameTypeVoidAndVoid() {
        assertTrue(VOID.sameType(VOID));
    }

    @Test
    public void testIsClass() {
        assertTrue(CLASS.isClass());
        assertFalse(VOID.isClass());
        assertFalse(FLOAT.isClass());
        assertFalse(INT.isClass());
        assertFalse(BOOLEAN.isClass());
    }

    @Test
    public void testIsClassOrNull() {
        assertTrue(CLASS.isClassOrNull());
        assertFalse(VOID.isClassOrNull());
        assertFalse(FLOAT.isClassOrNull());
        assertFalse(INT.isClassOrNull());
        assertFalse(BOOLEAN.isClassOrNull());
    }
}