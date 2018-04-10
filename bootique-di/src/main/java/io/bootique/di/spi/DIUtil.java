package io.bootique.di.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.inject.Qualifier;

/**
 * A helper class used by Bootique DI implementation.
 */
class DIUtil {

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

    private static Class<?> typeToClass(Type type) {
        if(type instanceof Class) {
            return  (Class<?>) type;
        } else if(type instanceof ParameterizedType){
            return  (Class<?>) ((ParameterizedType)type).getRawType();
        } else {
            return Object.class;
        }
    }

    /**
     * @param annotation instance
     * @return true if annotation is marked as {@link javax.inject.Qualifier}
     */
    static boolean isQualifyingAnnotation(Annotation annotation) {
        return null != annotation.annotationType().getAnnotation(Qualifier.class);
    }
}
