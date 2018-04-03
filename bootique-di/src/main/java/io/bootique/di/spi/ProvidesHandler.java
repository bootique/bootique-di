package io.bootique.di.spi;

import io.bootique.di.DIRuntimeException;
import io.bootique.di.Key;
import io.bootique.di.Module;
import io.bootique.di.Scope;

import javax.inject.Named;
import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Resolves {@link io.bootique.di.Provides} annotated methods in a module to a set of bindings.
 */
class ProvidesHandler {

    private DefaultInjector injector;
    private Class<? extends Annotation> providesAnnotation;

    public ProvidesHandler(DefaultInjector injector, Class<? extends Annotation> providesAnnotation) {
        this.injector = injector;
        this.providesAnnotation = providesAnnotation;
    }

    Collection<KeyBindingPair<?>> bindingsFromAnnotatedMethods(Module module) {

        Collection<KeyBindingPair<?>> bindings = Collections.emptyList();

        // consider annotated public methods in the module class and superclasses.

        for (Method m : module.getClass().getMethods()) {
            Annotation pa = m.getAnnotation(providesAnnotation);
            if (pa != null) {

                validateProvidesMethod(module, m);

                // change to mutable array on first match
                if (bindings.isEmpty()) {
                    bindings = new ArrayList<>();
                }

                // TODO: handle custom qualifier annotations per https://github.com/bootique/bootique-di/issues/4
                bindings.add(createBindingPair(module, m));
            }
        }

        return bindings;
    }

    private void validateProvidesMethod(Module module, Method method) {

        if (method.getReturnType().equals(Void.TYPE)) {
            throw new DIRuntimeException(
                    "Invalid 'void' provider method '%s' on module '%s' is void. To be a proper provider method, it must return a value",
                    method.getName(), module.getClass().getSimpleName());
        }
    }

    private <T> KeyBindingPair<T> createBindingPair(Module module, Method method) {

        Key<T> key = createKey(method.getGenericReturnType(), extractNameQualifier(method));
        Binding<T> binding = createBinding(module, method);

        return new KeyBindingPair<>(key, binding);
    }

    private String extractNameQualifier(Method method) {
        // TODO: generic @Qualifier annotation extractor
        Named named = method.getAnnotation(Named.class);
        return named != null ? named.value() : null;
    }

    private String extractNameQualifier(Annotation[] annotations) {
        // TODO: generic @Qualifier annotation extractor

        for (Annotation a : annotations) {
            if (a instanceof Named) {
                return ((Named) a).value();
            }
        }

        return null;
    }

    private <T> Key<T> createKey(Type bindingType, String nameQualifier) {

        if (bindingType instanceof Class) {
            return Key.get((Class<T>) bindingType, nameQualifier);
        }

        // TODO: implement support for generic types per https://github.com/bootique/bootique-di/issues/3
        throw new UnsupportedOperationException("Can't yet generate keys based on generic types: " + bindingType);
    }

    private <T> Binding<T> createBinding(Module module, Method method) {
        return new Binding<>(createProvider(module, method), createScope(method));
    }

    private <T> Provider<T> createProvider(Module module, Method method) {

        Provider<?>[] argumentProviders = createArgumentProviders(method);

        return () -> {
            try {
                int len = argumentProviders.length;
                Object[] arguments = new Object[len];

                for (int i = 0; i < len; i++) {
                    arguments[i] = argumentProviders[i].get();
                }

                // supporting both 'static' and instance methods..
                // TODO: accessibility - non-public inner and top-level classes... Does 'setAccessible' work across Java 9 modules?
                return (T) method.invoke(module, arguments);
            } catch (Exception e) {
                throw new DIRuntimeException("Error invoking provider method '%s' on module '%s'",
                        e,
                        method.getName(),
                        module.getClass().getSimpleName());
            }
        };
    }

    private Scope createScope(Method method) {
        // TODO: settle on scope default (no scope like Guice? Singleton like Cayenne?); process @Singleton annotation?
        return injector.getSingletonScope();
    }

    private Provider<?>[] createArgumentProviders(Method method) {

        Type[] params = method.getGenericParameterTypes();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        int len = params.length;
        Provider<?>[] providers = new Provider[len];

        for (int i = 0; i < len; i++) {

            String nameQualifier = extractNameQualifier(paramAnnotations[i]);
            Key<?> key = createKey(params[i], nameQualifier);

            // resolve the actual provider lazily
            providers[i] = () -> injector.getProvider(key).get();
        }

        return providers;
    }
}
