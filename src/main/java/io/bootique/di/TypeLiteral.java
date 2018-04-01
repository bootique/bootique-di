package io.bootique.di;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class TypeLiteral<T> {

    final Class<? super T> type;
    final String typeName;
    final String[] argumentsType;

    TypeLiteral(Class<? super T> type, Type... argumentsType) {
        this.type = type;
        this.typeName = type.getName();
        this.argumentsType = new String[argumentsType.length];
        for (int i = 0; i < argumentsType.length; i++) {
            if (argumentsType[i] instanceof ParameterizedType) {
                argumentsType[i] = ((ParameterizedType) argumentsType[i]).getRawType();
            }

            if (argumentsType[i] instanceof Class) {
                this.argumentsType[i] = ((Class) argumentsType[i]).getName();
            } else {
                this.argumentsType[i] = argumentsType[i].toString();
            }
        }
    }

    static <T> TypeLiteral<T> of(Class<T> type) {
        return new TypeLiteral<>(type);
    }

    static <T> TypeLiteral<List<T>> listOf(Class<? extends T> type) {
        return new TypeLiteral<>(List.class, type);
    }

    static <K, V> TypeLiteral<Map<K, V>> mapOf(Class<? extends K> keyType, Class<? extends V> valueType) {
        return new TypeLiteral<>(Map.class, keyType, valueType);
    }

    Class<? super T> getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TypeLiteral<?> that = (TypeLiteral<?>) o;

        if (!typeName.equals(that.typeName)) {
            return false;
        }
        return Arrays.equals(argumentsType, that.argumentsType);
    }

    @Override
    public int hashCode() {
        int result = typeName.hashCode();
        result = 31 * result + Arrays.hashCode(argumentsType);
        return result;
    }

    @Override
    public String toString() {
        String result = typeName;
        if (argumentsType.length > 0) {
            result += Arrays.toString(argumentsType);
        }
        return result;
    }
}
