package io.bootique.di.spi;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;

public class BinderAdapter implements Binder {

    private final io.bootique.di.Binder bootiqueBinder;

    private final InjectorAdapter injectorAdapter;

    BinderAdapter(io.bootique.di.Binder bootiqueBinder, InjectorAdapter injectorAdapter) {
        this.bootiqueBinder = bootiqueBinder;
        this.injectorAdapter = injectorAdapter;
    }

    @Override
    public <T> LinkedBindingBuilder<T> bind(Key<T> key) {
        return new BindingBuilderAdapter<>(this, DiUtils.toBootiqueKey(key));
    }

    @Override
    public <T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral) {
        return new BindingBuilderAdapter<>(this, DiUtils.toBootiqueKey(typeLiteral));
    }

    @Override
    public <T> AnnotatedBindingBuilder<T> bind(Class<T> type) {
        return new BindingBuilderAdapter<>(this, io.bootique.di.Key.get(type));
    }

    io.bootique.di.Binder getBootiqueBinder() {
        return bootiqueBinder;
    }

    public InjectorAdapter getInjectorAdapter() {
        return injectorAdapter;
    }
}
