package io.bootique.di.spi;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;

/**
 * Implementation of {@link com.google.inject.Injector} that
 * uses {@link io.bootique.di.Injector} internally.
 */
public class InjectorAdapter implements com.google.inject.Injector {

    private DefaultInjector bootiqueInjector;

    private BinderAdapter adapter;

    public InjectorAdapter(Iterable<? extends Module> modules) {

        // Create empty injector
        bootiqueInjector = new DefaultInjector();

        // Guice -> Bootique adapters
        io.bootique.di.Binder bootiqueBinder = new DefaultBinder(bootiqueInjector);
        adapter = new BinderAdapter(bootiqueBinder);
        ProvidesHandler providesHandler = new ProvidesHandler(bootiqueInjector, Provides.class);

        // Configure all modules manually
        modules.forEach(module -> {
            module.configure(adapter);
            providesHandler.bindingsFromAnnotatedMethods(module);
        });
    }

    @Override
    public void injectMembers(Object instance) {
        bootiqueInjector.injectMembers(instance);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Provider<T> getProvider(Key<T> key) {
        return () -> (T)bootiqueInjector.getProvider(DiUtils.toBootiqueKey(key)).get();
    }

    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        return () -> (T)bootiqueInjector.getProvider(type).get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getInstance(Key<T> key) {
        return bootiqueInjector.getInstance(DiUtils.toBootiqueKey(key));
    }

    @Override
    public <T> T getInstance(Class<T> type) {
        return bootiqueInjector.getInstance(type);
    }

    @Override
    public <T> Binding<T> getExistingBinding(Key<T> key) {
        io.bootique.di.spi.Binding<T> bootiqueBinding = bootiqueInjector.getBinding(DiUtils.toBootiqueKey(key));
        if(bootiqueBinding == null) {
            return null;
        }

        return new Binding<T>() {
            @Override
            public Key<T> getKey() {
                return key;
            }

            @Override
            public Provider<T> getProvider() {
                return () -> bootiqueBinding.getScoped().get();
            }
        };
    }
}
