package io.bootique.di;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.inject.Named;

/**
 * An object that encapsulates a key used to store and lookup DI bindings. Key is made of
 * a binding type and an optional qualifier (name or annotation).
 */
public class Key<T> {

    private static final KeyQualifier NO_QUALIFIER = new NoQualifier();

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

    /**
     * Creates a key for a qualified by annotation binding of a given type.
     *
     */
    public static <T> Key<T> get(Class<T> type, Class<? extends Annotation> annotationType) {
        return get(TypeLiteral.of(type), annotationType);
    }

    public static <T> Key<T> get(Class<T> type, Annotation annotationInstance) {
        return get(TypeLiteral.of(type), annotationInstance);
    }

    public static <T> Key<T> get(TypeLiteral<T> typeLiteral) {
        return get(typeLiteral, (String)null);
    }

    public static <T> Key<T> get(TypeLiteral<T> typeLiteral, String bindingName) {
        return new Key<>(typeLiteral, bindingName);
    }

    public static <T> Key<T> get(TypeLiteral<T> typeLiteral, Class<? extends Annotation> annotationType) {
        return new Key<>(typeLiteral, annotationType);
    }

    public static <T> Key<T> get(TypeLiteral<T> typeLiteral, Annotation annotationInstance) {
        return new Key<>(typeLiteral, annotationInstance);
    }

    public static <T> Key<List<T>> getListOf(Class<T> type) {
        return getListOf(type, null);
    }

    public static <T> Key<List<T>> getListOf(Class<T> type, String bindingName) {
        return get(TypeLiteral.listOf(type), bindingName);
    }

    public static <T> Key<Set<T>> getSetOf(Class<T> valueType) {
        return getSetOf(valueType, null);
    }

    public static <T> Key<Set<T>> getSetOf(Class<T> valueType, String bindingName) {
        return get(TypeLiteral.setOf(valueType), bindingName);
    }

    public static <K, V> Key<Map<K, V>> getMapOf(Class<K> keyType, Class<V> valueType) {
        return getMapOf(keyType, valueType, null);
    }

    public static <K, V> Key<Map<K, V>> getMapOf(Class<K> keyType, Class<V> valueType, String bindingName) {
        return get(TypeLiteral.mapOf(keyType, valueType), bindingName);
    }

    public static <K, V> Key<Map<K, V>> getMapOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return getMapOf(keyType, valueType, null);
    }

    public static <K, V> Key<Map<K, V>> getMapOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType, String bindingName) {
        return get(TypeLiteral.mapOf(keyType, valueType), bindingName);
    }

    private final TypeLiteral<T> typeLiteral;
    private final KeyQualifier keyQualifier;

    protected Key(TypeLiteral<T> type, String bindingName) {
        this.typeLiteral = Objects.requireNonNull(type, "Null key type");
        // empty non-null binding names are often passed from annotation defaults and are treated as no qualifier
        this.keyQualifier = bindingName != null && bindingName.length() > 0
                ? new NamedKeyQualifier(bindingName)
                : NO_QUALIFIER;
    }

    protected Key(TypeLiteral<T> typeLiteral, Class<? extends Annotation> annotationType) {
        this.typeLiteral = Objects.requireNonNull(typeLiteral, "Null key type");
        // null annotation type treated as no qualifier
        this.keyQualifier = annotationType == null
                ? NO_QUALIFIER
                : new AnnotationTypeQualifier(annotationType);
    }

    protected Key(TypeLiteral<T> typeLiteral, Annotation annotationInstance) {
        this.typeLiteral = Objects.requireNonNull(typeLiteral, "Null key type");
        if(annotationInstance == null) {
            // null annotation type treated as no qualifier
            this.keyQualifier = NO_QUALIFIER;
        } else if(annotationInstance instanceof Named) {
            // special case for @Named annotation
            String name = ((Named) annotationInstance).value();
            this.keyQualifier = name.length() > 0 ? new NamedKeyQualifier(name) : NO_QUALIFIER;
        } else {
            // general case
            this.keyQualifier = new AnnotationTypeQualifier(annotationInstance.annotationType());
        }
    }

    public TypeLiteral<T> getType() {
        return typeLiteral;
    }

    /**
     * Returns an optional name of the binding used to distinguish multiple bindings of
     * the same object type.
     */
    String getBindingName() {
        if (keyQualifier instanceof NamedKeyQualifier) {
            return ((NamedKeyQualifier) keyQualifier).getName();
        }
        return null;
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

            // compare additional qualifier
            return keyQualifier.equals(key.keyQualifier);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return 407 + 11 * typeLiteral.hashCode() + keyQualifier.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<BindingKey: ");
        buffer.append(typeLiteral);

        if (keyQualifier != NO_QUALIFIER) {
            buffer.append(", ").append(keyQualifier);
        }

        buffer.append('>');
        return buffer.toString();
    }

    /**
     * Marker interface for additional key qualifiers
     */
    interface KeyQualifier {
        // Implementation should define these methods, but we can't enforce it
        @Override
        boolean equals(Object other);

        @Override
        int hashCode();

        @Override
        String toString();
    }

    static final class NoQualifier implements KeyQualifier {
        private NoQualifier() {
        }

        @Override
        public boolean equals(Object other) {
            return this == other;
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public String toString() {
            return "no qualifier";
        }
    }

    static class AnnotationTypeQualifier implements KeyQualifier {

        private final Class<? extends Annotation> annotationType;

        AnnotationTypeQualifier(Class<? extends Annotation> annotationType) {
            this.annotationType = Objects.requireNonNull(annotationType);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }

            if (!(other instanceof AnnotationTypeQualifier)) {
                return false;
            }

            return ((AnnotationTypeQualifier) other).annotationType.equals(annotationType);
        }

        @Override
        public int hashCode() {
            return annotationType.hashCode();
        }

        @Override
        public String toString() {
            return "@" + annotationType.getName();
        }
    }

    static class NamedKeyQualifier implements KeyQualifier {

        private final String name;

        NamedKeyQualifier(String name) {
            this.name = Objects.requireNonNull(name);
        }

        private String getName() {
            return name;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }

            if (!(other instanceof NamedKeyQualifier)) {
                return false;
            }

            return ((NamedKeyQualifier) other).getName().equals(name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public String toString() {
            return "'" + name + "'";
        }
    }
}
