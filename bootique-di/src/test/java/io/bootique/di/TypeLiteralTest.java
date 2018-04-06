package io.bootique.di;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TypeLiteralTest {

    @Test
    public void testInstantiationEquivalence() {
        TypeLiteral<List<Integer>> typeLiteral1 = TypeLiteral.listOf(Integer.class);
        TypeLiteral<List<Integer>> typeLiteral2 = new TypeLiteral<List<Integer>>(){};
        TypeLiteral<?>             typeLiteral3 = TypeLiteral.of(List.class, Integer.class);

        assertEquals(typeLiteral1, typeLiteral2);
        assertEquals(typeLiteral1, typeLiteral3);
        assertEquals(typeLiteral2, typeLiteral3);
    }

    @Test
    public void testEqualGenericsWithBounds() {
        TypeLiteral<List<? super Integer>>   typeLiteral1 = new TypeLiteral<List<? super Integer>>(){};
        TypeLiteral<List<? super Integer>>   typeLiteral2 = new TypeLiteral<List<? super Integer>>(){};
        TypeLiteral<List<? extends Integer>> typeLiteral3 = new TypeLiteral<List<? extends Integer>>(){};
        TypeLiteral<List<? extends Integer>> typeLiteral4 = new TypeLiteral<List<? extends Integer>>(){};

        assertEquals(typeLiteral1,    typeLiteral2);
        assertEquals(typeLiteral3,    typeLiteral4);
        assertNotEquals(typeLiteral1, typeLiteral3);
        assertNotEquals(typeLiteral2, typeLiteral4);
    }

    @Test
    public void testEqualArrays() {
        TypeLiteral<int[]>     typeLiteral1 = new TypeLiteral<int[]>(){};
        TypeLiteral<int[]>     typeLiteral2 = TypeLiteral.of(int[].class);
        TypeLiteral<Integer[]> typeLiteral3 = new TypeLiteral<Integer[]>(){};
        TypeLiteral<Integer[]> typeLiteral4 = TypeLiteral.of(Integer[].class);

        assertEquals(typeLiteral1,    typeLiteral2);
        assertEquals(typeLiteral3,    typeLiteral4);
        assertNotEquals(typeLiteral1, typeLiteral3);
        assertNotEquals(typeLiteral2, typeLiteral4);
    }

    @Test
    public void testEqualGenericsArrays() {
        TypeLiteral<List<Integer>[]>         typeLiteral1 = new TypeLiteral<List<Integer>[]>(){};
        TypeLiteral<List<Integer>[]>         typeLiteral2 = new TypeLiteral<List<Integer>[]>(){};
        TypeLiteral<List<Number>[]>          typeLiteral3 = new TypeLiteral<List<Number>[]>(){};

        TypeLiteral<List<? super Integer>[]> typeLiteral4 = new TypeLiteral<List<? super Integer>[]>(){};
        TypeLiteral<List<? super Integer>[]> typeLiteral5 = new TypeLiteral<List<? super Integer>[]>(){};
        TypeLiteral<List<? super Number>[]>  typeLiteral6 = new TypeLiteral<List<? super Number>[]>(){};

        assertEquals(typeLiteral1,    typeLiteral2);
        assertNotEquals(typeLiteral1, typeLiteral3);
        assertNotEquals(typeLiteral2, typeLiteral3);

        assertEquals(typeLiteral4,    typeLiteral5);
        assertNotEquals(typeLiteral4, typeLiteral6);
        assertNotEquals(typeLiteral5, typeLiteral6);
    }

    @Test
    public void testNonEqualGenericsWithBounds() {
        TypeLiteral<List<Integer>>           typeLiteral1 = new TypeLiteral<List<Integer>>(){};
        TypeLiteral<List<? super Integer>>   typeLiteral2 = new TypeLiteral<List<? super Integer>>(){};
        TypeLiteral<List<? super Number>>    typeLiteral3 = new TypeLiteral<List<? super Number>>(){};
        TypeLiteral<List<? extends Integer>> typeLiteral4 = new TypeLiteral<List<? extends Integer>>(){};
        TypeLiteral<List<? extends Number>>  typeLiteral5 = new TypeLiteral<List<? extends Number>>(){};

        assertNotEquals(typeLiteral1, typeLiteral2);
        assertNotEquals(typeLiteral1, typeLiteral3);
        assertNotEquals(typeLiteral1, typeLiteral4);
        assertNotEquals(typeLiteral1, typeLiteral5);
        assertNotEquals(typeLiteral2, typeLiteral3);
        assertNotEquals(typeLiteral2, typeLiteral4);
        assertNotEquals(typeLiteral2, typeLiteral5);
        assertNotEquals(typeLiteral3, typeLiteral4);
        assertNotEquals(typeLiteral3, typeLiteral5);
        assertNotEquals(typeLiteral4, typeLiteral5);
    }

    @Test(expected = RuntimeException.class)
    public void testCreationFailure_NoGenericParam() {
        // No type parameters
        TypeLiteral<?> typeLiteral = new TypeLiteral(){};
    }

    @Test(expected = NullPointerException.class)
    public void testCreationFailure_NoType() {
        // No type parameters
        TypeLiteral<?> typeLiteral = TypeLiteral.of(null);
    }

    private static void assertEquals(TypeLiteral<?> literal1, TypeLiteral<?> literal2) {
        Assert.assertEquals(literal1.hashCode(), literal2.hashCode());
        Assert.assertEquals(literal1, literal2);
    }

    private static void assertNotEquals(TypeLiteral<?> literal1, TypeLiteral<?> literal2) {
        Assert.assertNotEquals(literal1.hashCode(), literal2.hashCode());
        Assert.assertNotEquals(literal1, literal2);
    }
}
