package io.bootique.di.spi;

import io.bootique.di.DIRuntimeException;
import io.bootique.di.Inject;
import io.bootique.di.Key;

import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

class ConstructorInjectingProvider<T> implements Provider<T> {

    private Constructor<? extends T> constructor;
    private DefaultInjector injector;
    private String[] bindingNames;

    ConstructorInjectingProvider(Class<? extends T> implementation,
                                 DefaultInjector injector) {

        initConstructor(implementation);

        if (constructor == null) {
            throw new DIRuntimeException(
                    "Can't find approprate constructor for implementation class '%s'",
                    implementation.getName());
        }

        this.constructor.setAccessible(true);
        this.injector = injector;
    }

    @SuppressWarnings("unchecked")
    private void initConstructor(Class<? extends T> implementation) {

        Constructor<?>[] constructors = implementation.getDeclaredConstructors();
        Constructor<?> lastMatch = null;
        int lastSize = -1;

        // pick the first constructor with all injection-annotated parameters, or the
        // default constructor; constructor with the longest parameter list is preferred
        // if multiple matches are found
        for (Constructor<?> constructor : constructors) {

            int size = constructor.getParameterTypes().length;
            if (size <= lastSize) {
                continue;
            }

            if (size == 0) {
                lastSize = 0;
                lastMatch = constructor;
                continue;
            }

            boolean injectable = true;
            for (Annotation[] annotations : constructor.getParameterAnnotations()) {

                boolean parameterInjectable = false;
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().equals(Inject.class)) {
                        parameterInjectable = true;
                        break;
                    }
                }

                if (!parameterInjectable) {
                    injectable = false;
                    break;
                }
            }

            if (injectable) {
                lastSize = size;
                lastMatch = constructor;
            }
        }

        if (lastMatch == null) {
            throw new DIRuntimeException(
                    "No applicable constructor is found for constructor injection in class '%s'",
                    implementation.getName());
        }

        // the cast is lame, but Class.getDeclaredConstructors() is not using
        // generics in Java 5 and using <?> in Java 6, creating compilation problems.
        this.constructor = (Constructor<? extends T>) lastMatch;

        Annotation[][] annotations = lastMatch.getParameterAnnotations();
        this.bindingNames = new String[annotations.length];
        for (int i = 0; i < annotations.length; i++) {

            Annotation[] parameterAnnotations = annotations[i];
            for (int j = 0; j < parameterAnnotations.length; j++) {
                Annotation annotation = parameterAnnotations[j];
                if (annotation.annotationType().equals(Inject.class)) {
                    Inject inject = (Inject) annotation;
                    bindingNames[i] = inject.value();
                    break;
                }
            }
        }
    }

    @Override
    public T get() {

        Class<?>[] constructorParameters = constructor.getParameterTypes();
        Type[] genericTypes = constructor.getGenericParameterTypes();
        Object[] args = new Object[constructorParameters.length];
        InjectionStack stack = injector.getInjectionStack();

        for (int i = 0; i < constructorParameters.length; i++) {
            args[i] = value(constructorParameters[i], genericTypes[i], bindingNames[i], stack);
        }

        try {
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new DIRuntimeException(
                    "Error instantiating class '%s'",
                    e,
                    constructor.getDeclaringClass().getName());
        }
    }

    protected Object value(Class<?> parameter, Type genericType, String bindingName, InjectionStack stack) {

        if (Provider.class.equals(parameter)) {

            Class<?> objectClass = DIUtil.parameterClass(genericType);

            if (objectClass == null) {
                throw new DIRuntimeException("Constructor provider parameter %s must be "
                        + "parameterized to be usable for injection", parameter.getName());
            }

            return injector.getProvider(Key.get(objectClass, bindingName));
        } else {

            Key<?> key = DIUtil.getKeyForTypeAndGenericType(parameter, genericType, bindingName);
            stack.push(key);
            try {
                return injector.getInstance(key);
            } finally {
                stack.pop();
            }
        }
    }

}
