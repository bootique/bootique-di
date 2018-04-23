package io.bootique.di.spi;

import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

class ConstructorInjectingDecoratorProvider<T> implements DecoratorProvider<T> {

    private final Class<? extends T> implementation;
    private final DefaultInjector injector;

    ConstructorInjectingDecoratorProvider(Class<? extends T> implementation, DefaultInjector injector) {
        this.implementation = implementation;
        this.injector = injector;
    }

    @Override
    public Provider<T> get(final Provider<T> undecorated) {

        return new ConstructorInjectingProvider<T>(implementation, injector) {
            @Override
            protected Object value(Class<?> parameter, Type genericType, Annotation bindingAnnotation, InjectionStack stack) {

                // delegate (possibly) injected as Provider
                if (Provider.class.equals(parameter)) {

                    Class<?> objectClass = DIUtil.parameterClass(genericType);

                    if (objectClass == null) {
                        return injector.throwException("Constructor provider parameter %s must be "
                                + "parameterized to be usable for injection", parameter.getName());
                    }

                    if (objectClass.isAssignableFrom(implementation)) {
                        return undecorated;
                    }
                }
                // delegate injected as value
                else if (parameter.isAssignableFrom(implementation)) {
                    return undecorated.get();
                }

                return super.value(parameter, genericType, bindingAnnotation, stack);
            }
        };
    }
}
