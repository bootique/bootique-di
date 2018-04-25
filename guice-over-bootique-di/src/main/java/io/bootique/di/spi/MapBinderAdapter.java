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
import io.bootique.di.MapBuilder;

public class MapBinderAdapter<K, V> {

    private MapBuilder<K, V> bootiqueMapBuilder;

    public MapBinderAdapter(com.google.inject.Binder guiceBinder, TypeLiteral<K> keyType, TypeLiteral<V> valueType,
                            Class<? extends Annotation> annotatedWith) {
        if(!(guiceBinder instanceof BinderAdapter)) {
            throw new IllegalArgumentException("Unexpected binder implementation: " + guiceBinder.getClass().getName());
        }
        Binder bootiqueBinder = ((BinderAdapter) guiceBinder).getBootiqueBinder();
        this.bootiqueMapBuilder = bootiqueBinder.bindMap(ConversionUtils.toTypeLiteral(keyType), ConversionUtils.toTypeLiteral(valueType), annotatedWith);
    }

    public LinkedBindingBuilder<V> addBinding(K key) {
        return new LinkedBindingBuilder<V>() {
            @Override
            public ScopedBindingBuilder to(Class<? extends V> implementation) {
                bootiqueMapBuilder.put(key, implementation);
                return this;
            }

            @Override
            public ScopedBindingBuilder to(TypeLiteral<? extends V> implementation) {
                bootiqueMapBuilder.put(key, ConversionUtils.toBootiqueKey(implementation));
                return this;
            }

            @Override
            public ScopedBindingBuilder to(Key<? extends V> targetKey) {
                bootiqueMapBuilder.put(key, ConversionUtils.toBootiqueKey(targetKey));
                return this;
            }

            @Override
            public void toInstance(V instance) {
                bootiqueMapBuilder.put(key, instance);
            }

            @Override
            public ScopedBindingBuilder toProvider(Provider<? extends V> provider) {
                throw new UnsupportedOperationException();
            }

            @Override
            public ScopedBindingBuilder toProvider(Class<? extends Provider<? extends V>> providerType) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void in(Class<? extends Annotation> scopeAnnotation) {
                if(scopeAnnotation == Singleton.class) {
                    bootiqueMapBuilder.inSingletonScope();
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
