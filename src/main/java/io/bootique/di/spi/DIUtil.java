
package io.bootique.di.spi;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import io.bootique.di.Key;

/**
 * A helper class used by Cayenne DI implementation.
 * 
 * @since 3.1
 */
class DIUtil {

    static Class<?> parameterClass(Type type) {

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] parameters = parameterizedType.getActualTypeArguments();

            if (parameters.length == 1) {
                return typeToClass(parameters[0]);
            }
        }

        return null;
    }

    static Class<?>[] allParametersClass(Type type) {
        Class<?>[] arr = null;

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] parameters = parameterizedType.getActualTypeArguments();
            arr = new Class[parameters.length];
            int i=0;
            for(Type next : parameters) {
                arr[i++] = typeToClass(next);
            }
        }

        return arr;
    }

    static Key<?> getKeyForTypeAndGenericType(Class<?> type, Type genericType, String bindingName) {
        if(List.class.isAssignableFrom(type)) {
            Class<?> objectClass = parameterClass(genericType);
            if(objectClass == null) {
                objectClass = Object.class;
            }
            return Key.getListOf(objectClass, bindingName);
        } else if(Map.class.isAssignableFrom(type)) {
            Class<?>[] classes = DIUtil.allParametersClass(genericType);
            if(classes == null) {
                classes = new Class[]{Object.class, Object.class};
            }

            return Key.getMapOf(classes[0], classes[1], bindingName);
        }

        return Key.get(type, bindingName);
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
