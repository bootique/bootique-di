package io.bootique.di.spi;

import io.bootique.di.DIRuntimeException;
import io.bootique.di.Injector;
import io.bootique.di.Key;
import io.bootique.di.Module;
import io.bootique.di.Scope;

import javax.inject.Provider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

/**
 * A default implementations of a DI injector.
 */
public class DefaultInjector implements Injector {

    private DefaultScope singletonScope;
    private Scope noScope;

    private final DefaultBinder binder;
    private final Map<Key<?>, Binding<?>> bindings;
    private final Map<Key<?>, Decoration<?>> decorations;
    private final ProvidesHandler providesHandler;

    private InjectionStack injectionStack;
    private Scope defaultScope;

    private boolean allowDynamicBinding;
    private boolean allowOverride;
    private boolean allowMethodInjection;

    private final InjectorPredicates predicates;

    DefaultInjector(Module... modules) throws DIRuntimeException {
        this(Collections.emptySet(), new InjectorPredicates(), modules);
    }

    public DefaultInjector(Set<Options> options, InjectorPredicates predicates, Module... modules) throws DIRuntimeException {
        this.predicates = predicates;

        this.singletonScope = new DefaultScope();
        this.noScope = NoScope.INSTANCE;
        if(options.contains(Options.NO_SCOPE_BY_DEFAULT)) {
            this.defaultScope = noScope;
        } else {
            this.defaultScope = singletonScope;
        }

        this.allowOverride = !options.contains(Options.DECLARED_OVERRIDE_ONLY);
        this.allowDynamicBinding = options.contains(Options.ENABLE_DYNAMIC_BINDINGS);
        this.allowMethodInjection = options.contains(Options.ENABLE_METHOD_INJECTION);

        this.bindings = new HashMap<>();
        this.decorations = new HashMap<>();
        this.injectionStack = new InjectionStack();

        this.providesHandler = new ProvidesHandler(this);
        this.binder = new DefaultBinder(this);

        // bind self for injector injection...
        binder.bind(Injector.class).toInstance(this);

        // bind modules
        if (modules != null && modules.length > 0) {
            for (Module module : modules) {
                module.configure(binder);
                providesHandler.bindingsFromAnnotatedMethods(module).forEach(p -> p.bind(this));
            }
        }

        applyDecorators();
    }

    InjectionStack getInjectionStack() {
        return injectionStack;
    }

    DefaultBinder getBinder() {
        return binder;
    }

    ProvidesHandler getProvidesHandler() {
        return providesHandler;
    }

    InjectorPredicates getPredicates() {
        return predicates;
    }

    @SuppressWarnings("unchecked")
    <T> Binding<T> getBinding(Key<T> key) throws DIRuntimeException {
        // may return null - this is intentionally allowed in this non-public method
        return (Binding<T>) bindings.get(Objects.requireNonNull(key, "Null key"));
    }

    <T> void putBinding(Key<T> bindingKey, Provider<T> provider) {
        putBinding(bindingKey, new Binding<>(provider, defaultScope, false));
    }

    <T> void putOptionalBinding(Key<T> bindingKey, Provider<T> provider) {
        putBinding(bindingKey, new Binding<>(provider, defaultScope, true));
    }

    <T> void putBinding(Key<T> bindingKey, Binding<T> binding) {
        Binding<?> oldBinding = bindings.put(bindingKey, binding);
        if(oldBinding != null && !oldBinding.isOptional() && !allowOverride) {
            throw new DIRuntimeException("Trying to override key %s.", bindingKey);
        }
    }

    <T> void putDecorationAfter(Key<T> bindingKey, DecoratorProvider<T> decoratorProvider) {

        @SuppressWarnings("unchecked")
        Decoration<T> decoration = (Decoration<T>) decorations.get(bindingKey);
        if (decoration == null) {
            decoration = new Decoration<>();
            decorations.put(bindingKey, decoration);
        }

        decoration.after(decoratorProvider);
    }

    <T> void putDecorationBefore(Key<T> bindingKey, DecoratorProvider<T> decoratorProvider) {

        @SuppressWarnings("unchecked")
        Decoration<T> decoration = (Decoration<T>) decorations.get(bindingKey);
        if (decoration == null) {
            decoration = new Decoration<>();
            decorations.put(bindingKey, decoration);
        }

        decoration.before(decoratorProvider);
    }

    <T> void changeBindingScope(Key<T> bindingKey, Scope scope) {
        if (scope == null) {
            scope = noScope;
        }

        Binding<?> binding = bindings.get(bindingKey);
        if (binding == null) {
            throw new DIRuntimeException("No existing binding for key " + bindingKey);
        }

        binding.changeScope(scope);
    }

    @Override
    public <T> T getInstance(Class<T> type) throws DIRuntimeException {
        return getProvider(type).get();
    }

    @Override
    public <T> T getInstance(Key<T> key) throws DIRuntimeException {
        return getProvider(key).get();
    }

    @Override
    public <T> Provider<T> getProvider(Class<T> type) throws DIRuntimeException {
        return getProvider(Key.get(type));
    }

    @Override
    public <T> Provider<T> getProvider(Key<T> key) throws DIRuntimeException {
        Binding<T> binding = getBinding(key);
        if (binding == null) {
            binding = createDynamicBinding(key);
        }

        return predicates.wrapProvider(binding.getScoped());
    }

    @SuppressWarnings("unchecked")
    private <T> Binding<T> createDynamicBinding(Key<T> key) {
        if(!allowDynamicBinding) {
            throw new DIRuntimeException("DI container has no binding for key %s and dynamic bindings are disabled.", key);
        }
        // create new binding
        // TODO: can we use something better than raw key type?
        binder.bind(key).to((Class)key.getType().getRawType());
        return getBinding(key);
    }

    @Override
    public void injectMembers(Object object) {
        Provider<Object> provider = new InstanceProvider<>(object);
        provider = new FieldInjectingProvider<>(provider, this);
        if(allowMethodInjection) {
            provider = new MethodInjectingProvider<>(provider, this);
        }
        provider.get();
    }

    @Override
    public void shutdown() {
        singletonScope.shutdown();
    }

    DefaultScope getSingletonScope() {
        return singletonScope;
    }

    Scope getDefaultScope() {
        return defaultScope;
    }

    Scope getNoScope() {
        return noScope;
    }

    boolean isMethodInjectionEnabled() {
        return allowMethodInjection;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    void applyDecorators() {
        for (Entry<Key<?>, Decoration<?>> e : decorations.entrySet()) {

            Binding b = bindings.get(e.getKey());
            if (b == null) {
                // TODO: print warning - decorator of a non-existing service..
                continue;
            }

            b.decorate(e.getValue());
        }
    }

    public enum Options {
        NO_SCOPE_BY_DEFAULT,
        DECLARED_OVERRIDE_ONLY,
        ENABLE_DYNAMIC_BINDINGS,
        ENABLE_METHOD_INJECTION
    }

}
