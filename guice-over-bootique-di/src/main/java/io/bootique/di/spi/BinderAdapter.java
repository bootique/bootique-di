package io.bootique.di.spi;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;

/**
 * @since 4.1
 */
public class BinderAdapter implements Binder {

    private final io.bootique.di.Binder bootiqueBinder;

    BinderAdapter(io.bootique.di.Binder bootiqueBinder) {
        this.bootiqueBinder = bootiqueBinder;
    }

    @Override
    public <T> LinkedBindingBuilder<T> bind(Key<T> key) {
        return new BindingBuilderAdapter<T>(bootiqueBinder.bind((Class)key.getTypeLiteral().getRawType()));
    }

    @Override
    public <T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral) {
        return new BindingBuilderAdapter<T>(bootiqueBinder.bind((Class)typeLiteral.getRawType()));
    }

    @Override
    public <T> AnnotatedBindingBuilder<T> bind(Class<T> type) {
        return new BindingBuilderAdapter<>(bootiqueBinder.bind(type));
    }

}
