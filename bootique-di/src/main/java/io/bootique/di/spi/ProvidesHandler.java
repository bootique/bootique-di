package io.bootique.di.spi;

import io.bootique.di.Key;
import io.bootique.di.Scope;
import io.bootique.di.TypeLiteral;

import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Resolves provider methods to a set of bindings. Provider methods are a part of a module class, each annotated
 * with a specified annotation. Usually this annotations is {@link io.bootique.di.Provides @Provides}.
 */
class ProvidesHandler {

    private final DefaultInjector injector;

    ProvidesHandler(DefaultInjector injector) {
        this.injector = injector;
    }

    Collection<KeyBindingPair<?>> bindingsFromAnnotatedMethods(Object module) {

        Collection<KeyBindingPair<?>> bindings = Collections.emptyList();

        // consider annotated methods in the module class
        for (Method m : module.getClass().getDeclaredMethods()) {
            if (injector.getPredicates().isProviderMethod(m)) {
                validateProvidesMethod(module, m);

                m.setAccessible(true);

                // change to mutable array on first match
                if (bindings.isEmpty()) {
                    bindings = new ArrayList<>();
                }

                bindings.add(createBindingPair(module, m));
            }
        }

        return bindings;
    }

    private void validateProvidesMethod(Object module, Method method) {

        if (method.getReturnType().equals(Void.TYPE)) {
            injector.throwException(
                    "Provider method '%s()' on module '%s' is void. To be a proper provider method, it must return a value",
                    method.getName(), module.getClass().getName());
        }
    }

    private <T> KeyBindingPair<T> createBindingPair(Object module, Method method) {

        Key<T> key = createKey(method.getGenericReturnType(), extractQualifier(method, method.getDeclaredAnnotations()));
        Binding<T> binding = createBinding(key, module, method);

        return new KeyBindingPair<>(key, binding);
    }

    private Annotation extractQualifier(Method method, Annotation[] annotations) {
        Annotation found = null;
        for (Annotation a : annotations) {
            if (injector.getPredicates().isQualifierAnnotation(a)) {
                if (found != null) {
                    injector.throwException("Multiple qualifying annotations found for method '%s()' or its parameter on module '%s'"
                            , method.getName()
                            , method.getDeclaringClass().getName());
                }
                found = a;
            }
        }

        return found;
    }

    private <T> Key<T> createKey(Type bindingType, Annotation qualifier) {
        if (isProviderType(bindingType)) {
            // Use provider generic argument as key
            bindingType = ((ParameterizedType) bindingType).getActualTypeArguments()[0];
        }
        return Key.get(TypeLiteral.of(bindingType), qualifier);
    }

    private boolean isProviderType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return injector.getPredicates().isProviderType(parameterizedType.getRawType());
        }
        return false;
    }

    private <T> Binding<T> createBinding(Key<T> key, Object module, Method method) {
        return new Binding<>(key, createProvider(key, module, method), createScope(method), false);
    }

    private <T> Provider<T> createProvider(Key<T> key, Object module, Method method) {
        Provider<?>[] argumentProviders = createArgumentProviders(method);
        Provider<T> provider = new ProvidesMethodProvider<>(injector, argumentProviders, method, module);
        return injector.wrapProvider(key, provider);
    }

    private Scope createScope(Method method) {
        // force singleton for annotated methods
        if (injector.getPredicates().isSingleton(method)) {
            return injector.getSingletonScope();
        }
        // otherwise use injector's default scope
        return injector.getDefaultScope();
    }

    private Provider<?>[] createArgumentProviders(Method method) {

        Type[] params = method.getGenericParameterTypes();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        int len = params.length;
        Provider<?>[] providers = new Provider[len];

        for (int i = 0; i < len; i++) {
            Annotation qualifier = extractQualifier(method, paramAnnotations[i]);
            Key<?> key = createKey(params[i], qualifier);

            if (isProviderType(params[i])) {
                // will resolve to provider of provider
                providers[i] = () -> injector.getProvider(key);
            } else {
                // resolve the actual provider lazily
                providers[i] = () -> injector.getProvider(key).get();
            }
        }

        return providers;
    }

    /**
     * Separate class just for better error reporting.
     * @param <T> provided type
     */
    private static class ProvidesMethodProvider<T> implements NamedProvider<T> {
        private final DefaultInjector injector;
        private final Provider<?>[] argumentProviders;
        private final Method method;
        private final Object module;

        private ProvidesMethodProvider(DefaultInjector injector, Provider<?>[] argumentProviders, Method method, Object module) {
            this.injector = injector;
            this.argumentProviders = argumentProviders;
            this.method = method;
            this.module = module;
        }

        @Override
        public T get() {
            int len = argumentProviders.length;
            Object[] arguments = new Object[len];

            for (int i = 0; i < len; i++) {
                injector.trace("Get argument %d for %s", i, getName());
                arguments[i] = argumentProviders[i].get();
            }

            injector.trace("Invoking %s", getName());
            try {
                @SuppressWarnings("unchecked")
                T result = (T) method.invoke(module, arguments);
                return result;
            } catch (Exception e) {
                injector.throwException("Error invoking %s", e, getName());
                return null;
            }
        }

        @Override
        public String getName() {
            return String.format("provider method '%s()' of module '%s'", method.getName(), module.getClass().getName());
        }
    }
}
