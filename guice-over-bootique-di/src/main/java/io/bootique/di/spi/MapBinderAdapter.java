/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.bootique.di.spi;

import java.lang.annotation.Annotation;

import javax.inject.Provider;

import com.google.inject.Key;
import com.google.inject.Scope;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import io.bootique.di.MapBuilder;

public class MapBinderAdapter<K, V> {

    private final BinderAdapter guiceBinderAdapter;
    private final MapBuilder<K, V> bootiqueMapBuilder;

    public MapBinderAdapter(com.google.inject.Binder guiceBinder, TypeLiteral<K> keyType, TypeLiteral<V> valueType,
                            Class<? extends Annotation> annotatedWith) {
        if(!(guiceBinder instanceof BinderAdapter)) {
            throw new IllegalArgumentException("Unexpected binder implementation: " + guiceBinder.getClass().getName());
        }
        this.guiceBinderAdapter = (BinderAdapter) guiceBinder;
        this.bootiqueMapBuilder = guiceBinderAdapter.getBootiqueBinder()
                .bindMap(ConversionUtils.toTypeLiteral(keyType), ConversionUtils.toTypeLiteral(valueType), annotatedWith);
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
