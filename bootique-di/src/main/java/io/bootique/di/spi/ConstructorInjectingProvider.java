package io.bootique.di.spi;

import io.bootique.di.DIRuntimeException;
import io.bootique.di.Key;
import io.bootique.di.TypeLiteral;

import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

class ConstructorInjectingProvider<T> implements Provider<T> {

    private Constructor<? extends T> constructor;
    private DefaultInjector injector;
    private Annotation[] bindingAnnotations;

    ConstructorInjectingProvider(Class<? extends T> implementation, DefaultInjector injector) {
        this.injector = injector;
        initConstructor(implementation);
    }

    @SuppressWarnings("unchecked")
    private void initConstructor(Class<? extends T> implementation) {

        Constructor<?>[] constructors = implementation.getDeclaredConstructors();
        Constructor<?> lastMatch = null;
        int lastSize = -1;

        // pick the first constructor annotated with @Inject, or the default constructor;
        // constructor with the longest parameter list is preferred if multiple matches are found.
        for (Constructor<?> constructor : constructors) {
            int size = constructor.getParameterCount();
            if (size <= lastSize) {
                continue;
            }

            if (size == 0) {
                lastSize = 0;
                lastMatch = constructor;
                continue;
            }

            if (injector.getPredicates().haveInjectAnnotation(constructor)) {
                lastSize = size;
                lastMatch = constructor;
            }
        }

        if (lastMatch == null) {
            throw new DIRuntimeException(
                    "No applicable constructor is found for constructor injection in class '%s'",
                    implementation.getName());
        }

        this.constructor = (Constructor<? extends T>) lastMatch;
        collectParametersQualifiers();
    }

    private void collectParametersQualifiers() {
        this.bindingAnnotations = new Annotation[constructor.getParameterCount()];
        Annotation[][] annotations = constructor.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            Annotation[] parameterAnnotations = annotations[i];
            for (Annotation annotation : parameterAnnotations) {
                if(injector.getPredicates().isQualifierAnnotation(annotation)) {
                    bindingAnnotations[i] = annotation;
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
            args[i] = value(constructorParameters[i], genericTypes[i], bindingAnnotations[i], stack);
        }

        try {
            this.constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new DIRuntimeException(
                    "Error instantiating class '%s'", e, constructor.getDeclaringClass().getName());
        }
    }

    protected Object value(Class<?> parameter, Type genericType, Annotation bindingAnnotation, InjectionStack stack) {

        if (injector.getPredicates().isProviderType(parameter)) {
            Type parameterType = DIUtil.getGenericParameterType(genericType);
            if (parameterType == null) {
                throw new DIRuntimeException("Constructor provider parameter %s must be "
                        + "parameterized to be usable for injection", parameter.getName());
            }
            return injector.getProvider(Key.get(TypeLiteral.of(parameterType), bindingAnnotation));
        } else {
            Key<?> key = Key.get(TypeLiteral.of(genericType), bindingAnnotation);
            stack.push(key);
            try {
                return injector.getInstance(key);
            } finally {
                stack.pop();
            }
        }
    }

}
