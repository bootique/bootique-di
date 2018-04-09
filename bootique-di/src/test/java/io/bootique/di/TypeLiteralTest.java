package io.bootique.di;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class TypeLiteralTest {

    @Test
    public void testBaseEqualsInvariants() {
        TypeLiteral<Integer> typeLiteral1 = TypeLiteral.of(Integer.TYPE);
        TypeLiteral<Integer> typeLiteral2 = typeLiteral1;
        Object object = new Object();

        assertEquals(typeLiteral1, typeLiteral2);
        Assert.assertNotEquals(typeLiteral1, object);
    }

    @Test
    public void testInstantiationEquivalence_List() {
        TypeLiteral<List<Integer>> typeLiteral1 = TypeLiteral.listOf(Integer.class);
        TypeLiteral<List<Integer>> typeLiteral2 = new TypeLiteral<List<Integer>>(){};
        TypeLiteral<?>             typeLiteral3 = TypeLiteral.of(List.class, Integer.class);
        TypeLiteral<?>             typeLiteral4 = TypeLiteral.listOf(TypeLiteral.of(Integer.class));

        assertEquals(typeLiteral1, typeLiteral2);
        assertEquals(typeLiteral1, typeLiteral3);
        assertEquals(typeLiteral1, typeLiteral4);
        assertEquals(typeLiteral2, typeLiteral3);
        assertEquals(typeLiteral2, typeLiteral4);
        assertEquals(typeLiteral3, typeLiteral4);
    }

    @Test
    public void testInstantiationEquivalence_Set() {
        TypeLiteral<Set<Integer>> typeLiteral1 = TypeLiteral.setOf(Integer.class);
        TypeLiteral<Set<Integer>> typeLiteral2 = new TypeLiteral<Set<Integer>>(){};
        TypeLiteral<?>            typeLiteral3 = TypeLiteral.of(Set.class, Integer.class);
        TypeLiteral<?>            typeLiteral4 = TypeLiteral.setOf(TypeLiteral.of(Integer.class));

        assertEquals(typeLiteral1, typeLiteral2);
        assertEquals(typeLiteral1, typeLiteral3);
        assertEquals(typeLiteral1, typeLiteral4);
        assertEquals(typeLiteral2, typeLiteral3);
        assertEquals(typeLiteral2, typeLiteral4);
        assertEquals(typeLiteral3, typeLiteral4);
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

    @Test
    public void testNestedGenerics() {
        TypeLiteral<Map<String, List<? extends Number>>>  typeLiteral1 = new TypeLiteral<Map<String, List<? extends Number>>>(){};
        TypeLiteral<Map<String, List<? extends Number>>>  typeLiteral2 = TypeLiteral.mapOf(new TypeLiteral<String>(){}, new TypeLiteral<List<? extends Number>>(){});
        TypeLiteral<Map<String, List<? extends Integer>>> typeLiteral3 = new TypeLiteral<Map<String, List<? extends Integer>>>(){};
        TypeLiteral<Map<String, List<? extends Integer>>> typeLiteral4 = TypeLiteral.mapOf(new TypeLiteral<String>(){}, new TypeLiteral<List<? extends Integer>>(){});

        assertEquals(typeLiteral1, typeLiteral2);
        assertEquals(typeLiteral3, typeLiteral4);

        assertNotEquals(typeLiteral1, typeLiteral3);
        assertNotEquals(typeLiteral1, typeLiteral4);
        assertNotEquals(typeLiteral2, typeLiteral3);
        assertNotEquals(typeLiteral2, typeLiteral4);
    }


    @Test(expected = DIRuntimeException.class)
    public void testCreationFailure_NoGenericParam() {
        // No type parameters
        new TypeLiteral(){};
    }

    @Test(expected = NullPointerException.class)
    public void testCreationFailure_NoType() {
        // No type parameters
        TypeLiteral.of(null);
    }

    @Test(expected = DIRuntimeException.class)
    public void testVariableTypeResolveFailure() {
        TypeLiteral<List<Integer>> typeLiteral = genericMethodForTest();
    }

    private static <T> TypeLiteral<List<T>> genericMethodForTest() {
        // Can't resolve variable type, should throw
        return new TypeLiteral<List<T>>(){};
    }

    private static void assertEquals(TypeLiteral<?> literal1, TypeLiteral<?> literal2) {
        Assert.assertEquals(literal1.hashCode(), literal2.hashCode());
        Assert.assertEquals(literal1.toString(), literal2.toString());
        Assert.assertEquals(literal1, literal2);
    }

    private static void assertNotEquals(TypeLiteral<?> literal1, TypeLiteral<?> literal2) {
        Assert.assertNotEquals(literal1.hashCode(), literal2.hashCode());
        Assert.assertNotEquals(literal1.toString(), literal2.toString());
        Assert.assertNotEquals(literal1, literal2);
    }
}
