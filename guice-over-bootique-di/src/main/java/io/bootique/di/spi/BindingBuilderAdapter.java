package io.bootique.di.spi;

import java.lang.annotation.Annotation;
import javax.inject.Provider;

import com.google.inject.Key;
import com.google.inject.Scope;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import io.bootique.di.BindingBuilder;

public class BindingBuilderAdapter<T> implements AnnotatedBindingBuilder<T> {

    final BinderAdapter binderAdapter;
    BindingBuilder<T> bindingBuilder;
    io.bootique.di.Key<T> bootiqueKey;

    BindingBuilderAdapter(BinderAdapter binderAdapter, io.bootique.di.Key<T> bootiqueKey) {
        this.binderAdapter = binderAdapter;
        this.bootiqueKey = bootiqueKey;
    }

    @Override
    public LinkedBindingBuilder<T> annotatedWith(Class<? extends Annotation> annotationType) {
        bootiqueKey = io.bootique.di.Key.get(bootiqueKey.getType(), annotationType);
        return this;
    }

    @Override
    public LinkedBindingBuilder<T> annotatedWith(Annotation annotation) {
        bootiqueKey = io.bootique.di.Key.get(bootiqueKey.getType(), annotation);
        return this;
    }

    @Override
    public ScopedBindingBuilder to(Class<? extends T> implementation) {
        getBinding().to(implementation);
        return this;
    }

    @Override
    public ScopedBindingBuilder to(TypeLiteral<? extends T> implementation) {
        getBinding().to(ConversionUtils.toBootiqueKey(implementation));
        return this;
    }

    @Override
    public ScopedBindingBuilder to(Key<? extends T> targetKey) {
        getBinding().to(ConversionUtils.toBootiqueKey(targetKey));
        return this;
    }

    @Override
    public void toInstance(T instance) {
        getBinding().toInstance(instance);
    }

    @Override
    public ScopedBindingBuilder toProvider(Provider<? extends T> provider) {
        getBinding().toProviderInstance(provider);
        return this;
    }

    @Override
    public ScopedBindingBuilder toProvider(Class<? extends Provider<? extends T>> providerType) {
        getBinding().toProvider(providerType);
        return this;
    }

    @Override
    public void in(Class<? extends Annotation> scopeAnnotation) {
        if(scopeAnnotation == Singleton.class) {
            getBinding().inSingletonScope();
        } else {
            throw new UnsupportedOperationException("Unable to use custom scope Annotation on Bootique DI");
        }
    }

    @Override
    public void in(Scope scope) {
        throw new UnsupportedOperationException("Unable to use custom Guice scope on Bootique DI");
    }

    @Override
    public void asEagerSingleton() {
        binderAdapter.getInjectorAdapter().markAsEagerSingleton(bootiqueKey);
    }

    BindingBuilder<T> getBinding() {
        if(bindingBuilder == null) {
            bindingBuilder = binderAdapter.getBootiqueBinder().bind(bootiqueKey);
        }
        return bindingBuilder;
    }
}
