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

public class BindingBuilderAdapter<T> implements AnnotatedBindingBuilder<T> {

    private final io.bootique.di.BindingBuilder<T> bootiqueBindingBuilder;

    BindingBuilderAdapter(io.bootique.di.BindingBuilder<T> bootiqueBindingBuilder) {
        this.bootiqueBindingBuilder = bootiqueBindingBuilder;
    }

    @Override
    public LinkedBindingBuilder<T> annotatedWith(Class<? extends Annotation> annotationType) {
        bootiqueBindingBuilder.annotatedWith(annotationType);
        return this;
    }

    @Override
    public LinkedBindingBuilder<T> annotatedWith(Annotation annotation) {
        bootiqueBindingBuilder.annotatedWith(annotation.annotationType());
        return this;
    }

    @Override
    public ScopedBindingBuilder to(Class<? extends T> implementation) {
        bootiqueBindingBuilder.to(implementation);
        return this;
    }

    @Override
    public ScopedBindingBuilder to(TypeLiteral<? extends T> implementation) {
        bootiqueBindingBuilder.to(DiUtils.toBootiqueKey(implementation));
        return this;
    }

    @Override
    public ScopedBindingBuilder to(Key<? extends T> targetKey) {
        bootiqueBindingBuilder.to(DiUtils.toBootiqueKey(targetKey));
        return this;
    }

    @Override
    public void toInstance(T instance) {
        bootiqueBindingBuilder.toInstance(instance);
    }

    @Override
    public ScopedBindingBuilder toProvider(Provider<? extends T> provider) {
        bootiqueBindingBuilder.toProviderInstance(provider);
        return this;
    }

    @Override
    public ScopedBindingBuilder toProvider(Class<? extends Provider<? extends T>> providerType) {
        bootiqueBindingBuilder.toProvider(providerType);
        return this;
    }

    @Override
    public void in(Class<? extends Annotation> scopeAnnotation) {
        if(scopeAnnotation == Singleton.class) {
            bootiqueBindingBuilder.inSingletonScope();
        } else {
            // TODO: unimplemented
        }
    }

    @Override
    public void in(Scope scope) {
        // TODO: unimplemented
    }

    @Override
    public void asEagerSingleton() {
        // TODO: should we support this?
        // it will launch creation of this binding automatically after configuration is done.
    }
}
