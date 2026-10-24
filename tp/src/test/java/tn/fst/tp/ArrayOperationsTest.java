package tn.fst.tp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArrayOperationsTest {

    @Test
    void testConstructor() {
        ArrayOperations operations = new ArrayOperations();
        assertNotNull(operations);
        assertNotNull(operations.array);
        assertEquals(0, operations.array.length);
    }

    @Test
    void testConstructorWithArray() {
        int[] input = {1, 0, 3};
        ArrayOperations operations = new ArrayOperations(input);
        assertNotNull(operations);
        assertArrayEquals(input, operations.array);
        assertSame(input, operations.array);
    }

    @Test
    void testConstructorWithNull() {
        ArrayOperations operations = new ArrayOperations(null);
        assertNotNull(operations);
        assertNull(operations.array);
    }
}

