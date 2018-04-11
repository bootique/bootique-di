package io.bootique.di.spi;

import io.bootique.di.DIRuntimeException;
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
    private final Class<? extends Annotation> providesAnnotation;

    ProvidesHandler(DefaultInjector injector, Class<? extends Annotation> providesAnnotation) {
        this.injector = injector;
        this.providesAnnotation = providesAnnotation;
    }

    Collection<KeyBindingPair<?>> bindingsFromAnnotatedMethods(Object module) {

        Collection<KeyBindingPair<?>> bindings = Collections.emptyList();

        // consider annotated methods in the module class
        for (Method m : module.getClass().getDeclaredMethods()) {
            Annotation pa = m.getAnnotation(providesAnnotation);
            if (pa != null) {

                validateProvidesMethod(module, m);

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
            throw new DIRuntimeException(
                    "Invalid 'void' provider method '%s()' on module '%s' is void. To be a proper provider method, it must return a value",
                    method.getName(), module.getClass().getSimpleName());
        }
    }

    private <T> KeyBindingPair<T> createBindingPair(Object module, Method method) {

        Key<T> key = createKey(method.getGenericReturnType(), extractQualifier(method, method.getDeclaredAnnotations()));
        Binding<T> binding = createBinding(module, method);

        return new KeyBindingPair<>(key, binding);
    }

    private Annotation extractQualifier(Method method, Annotation[] annotations) {
        Annotation found = null;
        for (Annotation a : annotations) {
            if (DIUtil.isQualifyingAnnotation(a)) {
                if (found != null) {
                    throw new DIRuntimeException("Multiple qualifying annotations found for method '%s()' or its parameter on module '%s'"
                            , method.getName()
                            , method.getDeclaringClass().getSimpleName());
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
            return parameterizedType.getRawType().equals(Provider.class);
        }
        return false;
    }

    private <T> Binding<T> createBinding(Object module, Method method) {
        return new Binding<>(createProvider(module, method), createScope(method));
    }

    private <T> Provider<T> createProvider(Object module, Method method) {

        Provider<?>[] argumentProviders = createArgumentProviders(method);

        return () -> {
            try {
                int len = argumentProviders.length;
                Object[] arguments = new Object[len];

                for (int i = 0; i < len; i++) {
                    arguments[i] = argumentProviders[i].get();
                }

                // supporting both 'static' and instance methods..
                method.setAccessible(true);
                @SuppressWarnings("unchecked")
                T result = (T) method.invoke(module, arguments);
                return result;
            } catch (Exception e) {
                throw new DIRuntimeException("Error invoking provider method '%s()' on module '%s'",
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
}
