package io.bootique.di;

import java.util.List;
import java.util.Map;

/**
 * An object that encapsulates a key used to store and lookup DI bindings. Key is made of
 * a binding type and an optional binding name.
 */
public class Key<T> {

    protected TypeLiteral<T> typeLiteral;
    protected String bindingName;

    protected Key(TypeLiteral<T> type, String bindingName) {
        if (type == null) {
            throw new NullPointerException("Null key type");
        }

        this.typeLiteral = type;

        // empty non-null binding names are often passed from annotation defaults and are
        // treated as null
        this.bindingName = bindingName != null && bindingName.length() > 0
                ? bindingName
                : null;
    }

    /**
     * Creates a key for a nameless binding of a given type.
     */
    public static <T> Key<T> get(Class<T> type) {
        return get(TypeLiteral.of(type));
    }

    /**
     * Creates a key for a named binding of a given type. 'bindingName' that is an empty
     * String is treated the same way as a null 'bindingName'. In both cases a nameless
     * binding key is created.
     */
    public static <T> Key<T> get(Class<T> type, String bindingName) {
        return new Key<>(TypeLiteral.of(type), bindingName);
    }

    public static <T> Key<T> get(TypeLiteral<T> typeLiteral) {
        return get(typeLiteral, null);
    }

    public static <T> Key<T> get(TypeLiteral<T> typeLiteral, String bindingName) {
        return new Key<>(typeLiteral, bindingName);
    }

    public static <T> Key<List<T>> getListOf(Class<T> type) {
        return getListOf(type, null);
    }

    public static <T> Key<List<T>> getListOf(Class<T> type, String bindingName) {
        return new Key<>(TypeLiteral.listOf(type), bindingName);
    }

    public static <K, V> Key<Map<K, V>> getMapOf(Class<K> keyType, Class<V> valueType) {
        return getMapOf(keyType, valueType, null);
    }

    public static <K, V> Key<Map<K, V>> getMapOf(Class<K> keyType, Class<V> valueType, String bindingName) {
        return new Key<>(TypeLiteral.mapOf(keyType, valueType), bindingName);
    }

    public static <K, V> Key<Map<K, V>> getMapOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return getMapOf(keyType, valueType, null);
    }

    public static <K, V> Key<Map<K, V>> getMapOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType, String bindingName) {
        return new Key<>(TypeLiteral.mapOf(keyType, valueType), bindingName);
    }

    public TypeLiteral<T> getType() {
        return typeLiteral;
    }

    /**
     * Returns an optional name of the binding used to distinguish multiple bindings of
     * the same object type.
     */
    public String getBindingName() {
        return bindingName;
    }

    @Override
    public boolean equals(Object object) {

        if (object == this) {
            return true;
        }

        if (object instanceof Key<?>) {
            Key<?> key = (Key<?>) object;

            // type is guaranteed to be not null, so skip null checking...
            if (!typeLiteral.equals(key.typeLiteral)) {
                return false;
            }

            // bindingName can be null, so take this into account
            if (bindingName != null) {
                return bindingName.equals(key.bindingName);
            } else {
                return key.bindingName == null;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {

        int hashCode = 407 + 11 * typeLiteral.hashCode();

        if (bindingName != null) {
            hashCode += bindingName.hashCode();
        }

        return hashCode;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<BindingKey: ");
        buffer.append(typeLiteral);

        if (bindingName != null) {
            buffer.append(", '").append(bindingName).append('\'');
        }

        buffer.append('>');
        return buffer.toString();
    }
}
