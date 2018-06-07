package io.bootique.di.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Qualifier;

import com.google.inject.Binding;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import io.bootique.di.DIBootstrap;

/**
 * Implementation of {@link com.google.inject.Injector} that
 * uses {@link io.bootique.di.Injector} internally.
 */
public class InjectorAdapter implements com.google.inject.Injector {

    private final DefaultInjector bootiqueInjector;
    private final BinderAdapter adapter;
    private final List<io.bootique.di.Key<?>> eagerSingletons;
    private final List<BindingBuilderAdapter<?>> bindingBuilders;

    public InjectorAdapter(Iterable<? extends Module> modules) {

        // Create customized injector
        eagerSingletons = new ArrayList<>();
        bindingBuilders = new ArrayList<>();
        //noinspection RedundantCast - it is not reduntant in provider wrapper
        bootiqueInjector = (DefaultInjector) DIBootstrap
                .injectorBuilder(b -> b.bind(Injector.class).toInstance(this))
                .defaultNoScope()
                .enableDynamicBindings()
                .withProvidesMethodPredicate(m -> m.isAnnotationPresent(Provides.class))
                .withQualifierPredicate(c -> c.isAnnotationPresent(Qualifier.class)
                        || c.isAnnotationPresent(BindingAnnotation.class))
                .withInjectAnnotationPredicate(o -> o.isAnnotationPresent(Inject.class)
                        || o.isAnnotationPresent(javax.inject.Inject.class))
                .withSingletonPredicate(el -> el.isAnnotationPresent(Singleton.class)
                        || el.isAnnotationPresent(javax.inject.Singleton.class))
                .withProviderPredicate(t -> Provider.class.equals(t)
                        || javax.inject.Provider.class.equals(t))
                .withProviderWrapper(p -> (Provider<Object>) p::get)
                .build();

        // Guice -> Bootique adapters
        adapter = new BinderAdapter(bootiqueInjector.getBinder(), this);
        // Configure all modules manually
        modules.forEach(this::installModule);

        // Create binding if it wasn't complete (i.e. binder.bind(Service.class);)
        bindingBuilders.forEach(BindingBuilderAdapter::getBinding);
        // Create eager singletons
        eagerSingletons.forEach(bootiqueInjector::getInstance);
    }

    public void installModule(Module module) {
        module.configure(adapter);
        bootiqueInjector.getProvidesHandler().bindingsFromAnnotatedMethods(module);
    }

    @Override
    public void injectMembers(Object instance) {
        bootiqueInjector.injectMembers(instance);
    }

    @Override
    public <T> Provider<T> getProvider(Key<T> key) {
        return () -> (T)bootiqueInjector.getProvider(ConversionUtils.toBootiqueKey(key)).get();
    }

    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        return () -> (T)bootiqueInjector.getProvider(type).get();
    }

    @Override
    public <T> T getInstance(Key<T> key) {
        return bootiqueInjector.getInstance(ConversionUtils.toBootiqueKey(key));
    }

    @Override
    public <T> T getInstance(Class<T> type) {
        return bootiqueInjector.getInstance(type);
    }

    @Override
    public <T> Binding<T> getExistingBinding(Key<T> key) {
        io.bootique.di.spi.Binding<T> bootiqueBinding = bootiqueInjector.getBinding(ConversionUtils.toBootiqueKey(key));
        if(bootiqueBinding == null) {
            return null;
        }

        return toGuiceBinding(key, bootiqueBinding);
    }

    void registerBindingBuilder(BindingBuilderAdapter<?> adapter) {
        this.bindingBuilders.add(adapter);
    }

    private <T> Binding<T> toGuiceBinding(Key<T> key, io.bootique.di.spi.Binding<T> bootiqueBinding) {
        return new Binding<T>() {
            @Override
            public Key<T> getKey() {
                return key;
            }

            @Override
            public Provider<T> getProvider() {
                return () -> {
                    if(bootiqueBinding.getOriginal() == null) {
                        return bootiqueInjector.getProvider(bootiqueBinding.getKey()).get();
                    }
                    return bootiqueBinding.getScoped().get();
                };
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<Binding<T>> findBindingsByType(TypeLiteral<T> type) {
        io.bootique.di.TypeLiteral<T> bootiqueType = ConversionUtils.toTypeLiteral(type);
        List<Binding<T>> result = new ArrayList<>();
        // TODO: this is slow, should be probably cached
        for(Map.Entry<io.bootique.di.Key<?>, io.bootique.di.spi.Binding<?>> entry
                : bootiqueInjector.getAllBindings().entrySet()) {
            if(entry.getKey().getType().equals(bootiqueType)) {
                result.add(toGuiceBinding(
                        ConversionUtils.toGuiceKey(type, (io.bootique.di.Key)entry.getKey())
                        , (io.bootique.di.spi.Binding)entry.getValue())
                );
            }
        }
        return result;
    }

    <T> void markAsEagerSingleton(io.bootique.di.Key<T> bootiqueKey) {
        io.bootique.di.spi.Binding<T> binding = bootiqueInjector.getBinding(bootiqueKey);
        if(binding != null) {
            binding.changeScope(bootiqueInjector.getSingletonScope());
        } else {
            bootiqueInjector.getBinder().bind(bootiqueKey).inSingletonScope();
        }
        eagerSingletons.add(bootiqueKey);
    }
}
