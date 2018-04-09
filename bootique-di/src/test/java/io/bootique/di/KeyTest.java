
package io.bootique.di;

import java.util.List;

import io.bootique.di.Key;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class KeyTest {

    @Test
    public void testBaseEquals() {
        Key<String> key1 = Key.get(String.class);
        Key<String> key2 = key1;
        Object key3 = new Object();

        assertEquals(key1, key2);
        assertEquals(key2, key1);
        assertNotEquals(key1, key3);
    }

    @Test
    public void testEquals() {
        Key<String> key1 = Key.get(String.class);
        Key<String> key2 = Key.get(String.class);
        Key<Integer> key3 = Key.get(Integer.class);
        Key<Integer> key31 = Key.get(Integer.class, "");
        Key<Integer> key4 = Key.get(Integer.class, "a");
        Key<Integer> key5 = Key.get(Integer.class, "a");
        Key<Integer> key6 = Key.get(Integer.class, "b");
        Key<String> key7 = Key.get(String.class, "a");

        assertNull(key1.getBindingName());
        assertTrue(key1.equals(key2));

        assertFalse(key1.equals(key3));

        assertTrue(key3.equals(key31));
        assertNull(key3.getBindingName());
        assertTrue(key31.equals(key3));
        assertNull(key31.getBindingName());

        assertFalse(key3.equals(key4));
        assertFalse(key4.equals(key3));
        assertEquals("a", key4.getBindingName());

        assertTrue(key4.equals(key5));
        assertEquals("a", key5.getBindingName());
        assertTrue(key5.equals(key4));

        assertFalse(key5.equals(key6));
        assertEquals("b", key6.getBindingName());
        assertFalse(key6.equals(key5));

        assertFalse(key4.equals(key7));
        assertEquals("a", key7.getBindingName());
        assertFalse(key7.equals(key4));
    }

    @Test
    public void testListKeysEquals() {
        Key<List<Integer>> key1 = Key.getListOf(Integer.class);
        Key<List<String>> key2 = Key.getListOf(String.class);
        Key<List<Integer>> key3 = Key.getListOf(Integer.class);
        Key<List<String>> key4 = Key.getListOf(String.class);

        assertNotEquals(key1, key2);
        assertNotEquals(key3, key4);

        assertEquals(key1, key3);
        assertEquals(key1, key1);
        assertEquals(key2, key4);
        assertEquals(key4, key4);

        // Name is suppressing generic type, to keep backward compatibility.
        Key key5 = Key.getListOf(Object.class, "xyz");
        Key key6 = Key.getListOf(Object.class, "abc");
        assertNotEquals(key5, key6);

        Key key7 = Key.getListOf(Integer.class, "xyz");
        Key key8 = Key.getListOf(Integer.class, "abc");
        assertNotEquals(key7, key8);
        assertNotEquals(key5, key7);

        Key key9 = Key.get(List.class, "xyz");
        assertNotEquals(key7, key9);
     }

    @Test
    public void testHashCode() {
        Key<String> key1 = Key.get(String.class);
        Key<String> key2 = Key.get(String.class);
        Key<Integer> key3 = Key.get(Integer.class);
        Key<Integer> key4 = Key.get(Integer.class, "a");
        Key<Integer> key5 = Key.get(Integer.class, "a");
        Key<Integer> key6 = Key.get(Integer.class, "b");
        Key<String> key7 = Key.get(String.class, "a");

        assertTrue(
                "generated different hashcode on second inocation",
                key1.hashCode() == key1.hashCode());
        assertTrue(key1.hashCode() == key2.hashCode());
        assertTrue(key4.hashCode() == key5.hashCode());

        // these are not technically required for hashCode() validity, but as things stand
        // now, these tests will all succeed.
        assertFalse(key1.hashCode() == key3.hashCode());
        assertFalse(key4.hashCode() == key3.hashCode());
        assertFalse(key5.hashCode() == key6.hashCode());
        assertFalse(key7.hashCode() == key4.hashCode());
    }

    @Test
    public void testToString() {
        assertEquals("<BindingKey: java.lang.String>",
                Key.get(String.class).toString());
        assertEquals("<BindingKey: java.lang.String, 'xyz'>",
                Key.get(String.class, "xyz").toString());
        assertEquals("<BindingKey: java.util.List[java.lang.String]>",
                Key.getListOf(String.class).toString());
        assertEquals("<BindingKey: java.util.List[java.lang.String], 'xyz'>",
                Key.getListOf(String.class, "xyz").toString());
    }
}
