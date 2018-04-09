package io.bootique.di;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * This class represents any generic type T, as there is no support for this in Java.
 * <p>
 * Usage: <pre>
 *     TypeLiteralg&lt;List&lt;Integer&gt;&gt; type = new TypeLiteral&lt;List&lt;Integer&gt;&gt;(){};
 * </pre>
 */
public class TypeLiteral<T> {

    private static final Class WILDCARD_MARKER = WildcardMarker.class;

    private final Class<? super T> type;
    private final String typeName;
    private final String[] argumentTypes;

    public static <T> TypeLiteral<T> of(Class<T> type) {
        return new TypeLiteral<>(type);
    }

    /**
     * Creates TypeLiteral that represents List&lt;T&gt; type.
     */
    public static <T> TypeLiteral<List<T>> listOf(Class<? extends T> type) {
        return new TypeLiteral<>(List.class, type);
    }

    /**
     * Creates TypeLiteral that represents Map&lt;K, V&lt; type.
     */
    public static <K, V> TypeLiteral<Map<K, V>> mapOf(Class<? extends K> keyType, Class<? extends V> valueType) {
        return new TypeLiteral<>(Map.class, keyType, valueType);
    }

    /**
     * Creates TypeLiteral that represents Map&lt;K, V&lt; type.
     */
    public static <K, V> TypeLiteral<Map<K, V>> mapOf(TypeLiteral<? extends K> keyType, TypeLiteral<? extends V> valueType) {
        return new TypeLiteral<>(Map.class, keyType.toString(), valueType.toString());
    }

    @SuppressWarnings("unchecked")
    public static <T> TypeLiteral<T> of(Type type) {
        return new TypeLiteral<>(type);
    }

    public static <T> TypeLiteral<T> of(Class<T> rawType, Type... parameters) {
        return new TypeLiteral<>(rawType, parameters);
    }

    @SuppressWarnings("unchecked")
    protected TypeLiteral() {
        Type genericType = getGenericSuperclassType(getClass());
        this.type = (Class<T>)getRawType(genericType);
        this.typeName = type.getName();
        Type[] argumentTypes = getArgumentTypes(genericType);
        this.argumentTypes = new String[argumentTypes.length];
        initArgumentTypes(argumentTypes);
    }

    @SuppressWarnings("unchecked")
    private TypeLiteral(Type type) {
        this.type = (Class<T>)getRawType(Objects.requireNonNull(type, "No type"));
        this.typeName = this.type.getName();
        Type[] argumentTypes = getArgumentTypes(type);
        this.argumentTypes = new String[argumentTypes.length];
        initArgumentTypes(argumentTypes);
    }

    private TypeLiteral(Class<? super T> type, String... argumentTypes) {
        this.type = type;
        this.typeName = type.getName();
        this.argumentTypes = argumentTypes;
    }

    private TypeLiteral(Class<? super T> type, Type... argumentsType) {
        this.type = Objects.requireNonNull(type, "No class");
        this.typeName = type.getName();
        this.argumentTypes = new String[argumentsType.length];
        initArgumentTypes(argumentsType);
    }

    private void initArgumentTypes(Type... argumentsType) {
        for (int i = 0; i < argumentsType.length; i++) {
            // recursively resolve argument types..
            this.argumentTypes[i] = new TypeLiteral<>(argumentsType[i]).toString();
        }
    }

    private static Type getGenericSuperclassType(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (!(superclass instanceof ParameterizedType)) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return parameterized.getActualTypeArguments()[0];
    }

    Class<? super T> getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TypeLiteral)) {
            return false;
        }

        TypeLiteral<?> that = (TypeLiteral<?>) o;
        if (!typeName.equals(that.typeName)) {
            return false;
        }
        return Arrays.equals(argumentTypes, that.argumentTypes);
    }

    @Override
    public int hashCode() {
        int result = typeName.hashCode();
        result = 31 * result + Arrays.hashCode(argumentTypes);
        return result;
    }

    @Override
    public String toString() {
        String result = typeName;
        if (argumentTypes.length > 0) {
            result += Arrays.toString(argumentTypes);
        }
        return result;
    }

    private static Type[] getArgumentTypes(Type type) {
        if(type instanceof Class) {
            return new Type[0];
        } if(type instanceof ParameterizedType) {
            return ((ParameterizedType)type).getActualTypeArguments();
        } else if (type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) type;
            Type componentType = genericArrayType.getGenericComponentType();
            if(!(componentType instanceof ParameterizedType)) {
                throw new IllegalArgumentException("Expected ParameterizedType, got " + componentType.toString());
            }
            return  ((ParameterizedType) componentType).getActualTypeArguments();
        } else if(type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType)type;
            Type[] lowerBounds = wildcardType.getLowerBounds();
            Type[] upperBounds = wildcardType.getUpperBounds();
            Type lower = lowerBounds.length > 0 ? wildcardType.getLowerBounds()[0] : Object.class;
            Type upper = upperBounds.length > 0 ? wildcardType.getUpperBounds()[0] : Object.class;
            return new Type[]{lower, upper};
        } else {
            return new Type[]{type};
        }
    }

    private static Class getRawType(Type type) {
        if(type instanceof Class) {
            return (Class)type;
        } if(type instanceof ParameterizedType) {
            return (Class)((ParameterizedType) type).getRawType();
        } else if (type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) type;
            Type componentType = genericArrayType.getGenericComponentType();
            if(!(componentType instanceof ParameterizedType)) {
                throw new IllegalArgumentException("Expected ParameterizedType, got " + componentType.toString());
            }
            Class rawType = (Class)((ParameterizedType) componentType).getRawType();
            return Array.newInstance(rawType, 0).getClass();
        } else if(type instanceof WildcardType) {
            return WILDCARD_MARKER;
        } else {
            return Object.class;
        }
    }

    /**
     * Marker interface for WildcardType
     */
    private interface WildcardMarker{
    }
}
