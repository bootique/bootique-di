package io.bootique.di.spi;

import java.lang.annotation.Annotation;
import javax.inject.Provider;

import com.google.inject.Key;
import com.google.inject.Scope;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import io.bootique.di.Binder;
import io.bootique.di.SetBuilder;

public class SetBinderAdapter<T> {

    private SetBuilder<T> bootiqueSetBuilder;

    public SetBinderAdapter(com.google.inject.Binder guiceBinder, Key<T> key) {
        if(!(guiceBinder instanceof BinderAdapter)) {
            throw new IllegalArgumentException("Unexpected binder implementation: " + guiceBinder.getClass().getName());
        }
        Binder bootiqueBinder = ((BinderAdapter) guiceBinder).getBootiqueBinder();
        this.bootiqueSetBuilder = bootiqueBinder.bindSet(ConversionUtils.toBootiqueKey(key).getType(), key.getAnnotationType());
    }

    public LinkedBindingBuilder<T> addBinding() {
        return new LinkedBindingBuilder<T>() {
            @Override
            public ScopedBindingBuilder to(Class<? extends T> implementation) {
                bootiqueSetBuilder.add(implementation);
                return this;
            }

            @Override
            public void toInstance(T instance) {
                bootiqueSetBuilder.add(instance);
            }

            @Override
            public ScopedBindingBuilder to(TypeLiteral<? extends T> implementation) {
                bootiqueSetBuilder.add(ConversionUtils.toBootiqueKey(implementation));
                return this;
            }

            @Override
            public ScopedBindingBuilder to(Key<? extends T> targetKey) {
                bootiqueSetBuilder.add(ConversionUtils.toBootiqueKey(targetKey));
                return this;
            }

            @Override
            public ScopedBindingBuilder toProvider(Provider<? extends T> provider) {
                throw new UnsupportedOperationException();
            }

            @Override
            public ScopedBindingBuilder toProvider(Class<? extends Provider<? extends T>> providerType) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void in(Class<? extends Annotation> scopeAnnotation) {
                if(scopeAnnotation == Singleton.class) {
                    bootiqueSetBuilder.inSingletonScope();
                } else {
                    throw new UnsupportedOperationException();
                }
            }

            @Override
            public void in(Scope scope) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void asEagerSingleton() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
