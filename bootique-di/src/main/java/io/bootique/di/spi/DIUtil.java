package io.bootique.di.spi;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.inject.Provider;

/**
 * A helper class used by Bootique DI implementation.
 */
class DIUtil {

    /**
     * @param provider to get name for
     * @return name of provider
     */
    static String getProviderName(Provider<?> provider) {
        if(provider instanceof NamedProvider) {
            return ((NamedProvider<?>) provider).getName();
        }
        return provider.getClass().getName();
    }

    static Type getGenericParameterType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] parameters = parameterizedType.getActualTypeArguments();

            if (parameters.length == 1) {
                return parameters[0];
            }
        }

        return null;
    }

    static Class<?> parameterClass(Type type) {
        Type parameterType = getGenericParameterType(type);
        if(parameterType == null) {
            return null;
        }

        return typeToClass(parameterType);
    }

    static Class<?> typeToClass(Type type) {
        if(type instanceof Class) {
            return  (Class<?>) type;
        } else if(type instanceof ParameterizedType){
            return  (Class<?>) ((ParameterizedType)type).getRawType();
        } else {
            return Object.class;
        }
    }

}
