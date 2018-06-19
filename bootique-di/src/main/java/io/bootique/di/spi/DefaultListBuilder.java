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

import io.bootique.di.Key;
import io.bootique.di.ListBuilder;

import javax.inject.Provider;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

class DefaultListBuilder<T> extends DICollectionBuilder<List<T>, T> implements ListBuilder<T> {

    protected static final AtomicLong incrementer = new AtomicLong();

    DefaultListBuilder(Key<List<T>> bindingKey, DefaultInjector injector) {
        super(bindingKey, injector);

        // trigger initialization of the ListProvider right away, as we need to bind an
        // empty list even if the user never calls 'put'
        findOrCreateListProvider();
    }

    @Override
    public ListBuilder<T> add(Class<? extends T> interfaceType) {
        Provider<? extends T> provider = createTypeProvider(interfaceType);
        findOrCreateListProvider().add(Key.get(interfaceType), provider);
        return this;
    }

    @Override
    public ListBuilder<T> addAfter(Class<? extends T> interfaceType, Class<? extends T> afterType) {
        Provider<? extends T> provider = createTypeProvider(interfaceType);
        findOrCreateListProvider().addAfter(Key.get(interfaceType), provider, Key.get(afterType));
        return this;
    }

    @Override
    public ListBuilder<T> insertBefore(Class<? extends T> interfaceType, Class<? extends T> beforeType) {
        Provider<? extends T> provider = createTypeProvider(interfaceType);
        findOrCreateListProvider().insertBefore(Key.get(interfaceType), provider, Key.get(beforeType));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListBuilder<T> add(T value) {
        Key<? extends T> key = Key.get((Class<? extends T>) value.getClass(),
                String.valueOf(incrementer.getAndIncrement()));
        findOrCreateListProvider().add(key, createInstanceProvider(value));
        return this;
    }

    @Override
    public ListBuilder<T> add(Key<? extends T> valueKey) {
        findOrCreateListProvider().add(valueKey, getByKeyProvider(valueKey));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListBuilder<T> addAfter(T value, Class<? extends T> afterType) {
        Key<? extends T> key = Key.get((Class<? extends T>) value.getClass(),
                String.valueOf(incrementer.getAndIncrement()));
        findOrCreateListProvider().addAfter(key, createInstanceProvider(value), Key.get(afterType));
        return this;
    }

    @Override
    public ListBuilder<T> addAfter(Key<? extends T> valueKey, Class<? extends T> afterType) {
        findOrCreateListProvider().addAfter(valueKey, getByKeyProvider(valueKey), Key.get(afterType));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListBuilder<T> insertBefore(T value, Class<? extends T> beforeType) {
        Key<? extends T> key = Key.get((Class<? extends T>) value.getClass(),
                String.valueOf(incrementer.getAndIncrement()));
        findOrCreateListProvider().insertBefore(key, createInstanceProvider(value), Key.get(beforeType));
        return this;
    }

    @Override
    public ListBuilder<T> insertBefore(Key<? extends T> valueKey, Class<? extends T> beforeType) {
        findOrCreateListProvider().insertBefore(valueKey, getByKeyProvider(valueKey), Key.get(beforeType));
        return this;
    }

    @Override
    public ListBuilder<T> addAll(Collection<T> values) {
        findOrCreateListProvider().addAll(createProviderMap(values));
        return this;
    }

    @Override
    public ListBuilder<T> addAllAfter(Collection<T> values, Class<? extends T> afterType) {
        findOrCreateListProvider().addAllAfter(createProviderMap(values), Key.get(afterType));
        return this;
    }

    @Override
    public ListBuilder<T> insertAllBefore(Collection<T> values, Class<? extends T> beforeType) {
        findOrCreateListProvider().insertAllBefore(createProviderMap(values), Key.get(beforeType));
        return this;
    }

    private Map<Key<? extends T>, Provider<? extends T>> createProviderMap(Collection<T> objects) {
        Map<Key<? extends T>, Provider<? extends T>> keyProviderMap = new LinkedHashMap<>();
        for (T object : objects) {
            @SuppressWarnings("unchecked")
            Class<? extends T> objectType = (Class<? extends T>) object.getClass();
            keyProviderMap.put(
                    Key.get(objectType, String.valueOf(incrementer.getAndIncrement())),
                    createInstanceProvider(object));
        }

        return keyProviderMap;
    }

    private ListProvider<T> findOrCreateListProvider() {

        ListProvider<T> provider;

        Binding<List<T>> binding = injector.getBinding(bindingKey);
        if (binding == null) {
            provider = new ListProvider<>(injector);
            injector.putBinding(bindingKey, provider);
        } else {
            if (injector.isInjectionTraceEnabled()) {
                provider = ((TraceableProvider<List<T>>) binding.getOriginal()).unwrap();
            } else {
                provider = (ListProvider<T>) binding.getOriginal();
            }
        }

        return provider;
    }
}
