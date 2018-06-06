/**
 *    Licensed to the ObjectStyle LLC under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ObjectStyle LLC licenses
 *  this file to you under the Apache License, Version 2.0 (the
 *  “License”); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.bootique.di.spi;

import io.bootique.di.Key;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

class ListProvider<T> implements Provider<List<T>> {

    private final Map<Key<? extends T>, Provider<? extends T>> providers;
    private final DIGraph<Key<? extends T>> graph;
    private final DefaultInjector injector;

    private volatile Collection<Key<? extends T>> keysInInsertOrder;
    private volatile boolean dirty;

    ListProvider(DefaultInjector injector) {
        this.providers = new ConcurrentHashMap<>();
        this.graph = new DIGraph<>();
        this.injector = injector;
        this.keysInInsertOrder = Collections.emptyList();
    }

    @Override
    public List<T> get() {
        Collection<Key<? extends T>> insertOrder = getKeysInInsertOrder();
        List<T> list = new ArrayList<>(insertOrder.size());
        for (Key<? extends T> key : insertOrder) {
            injector.trace(() -> "Resolving list element " + key);
            list.add(providers.get(key).get());
        }

        return list;
    }

    private Collection<Key<? extends T>> getKeysInInsertOrder() {
        boolean dirty = this.dirty;

        if(dirty) {
            synchronized (graph) {
                dirty = this.dirty;
                if(!dirty) {
                    return keysInInsertOrder;
                }

                // need to resort keys
                injector.trace(() -> "Sorting list elements");
                try {
                    // use CopyOnWriteArrayList as an additional protection,
                    // there should be no modifications of it's values
                    keysInInsertOrder = new CopyOnWriteArrayList<>(graph.topSort());
                } catch (IllegalStateException e) {
                    return injector.throwException(e.getMessage());
                }

                if (keysInInsertOrder.size() != providers.size()) {
                    List<Key<? extends T>> emptyKeys = new ArrayList<>();

                    for (Key<? extends T> key : keysInInsertOrder) {
                        if (!providers.containsKey(key)) {
                            emptyKeys.add(key);
                        }
                    }

                    return injector.throwException("DI list has no providers for keys: %s", emptyKeys);
                }

                this.dirty = false;
            }
        }

        return keysInInsertOrder;
    }

    void add(Key<? extends T> key, Provider<? extends T> provider) {
        providers.put(key, provider);
        synchronized (graph) {
            graph.add(key);
            dirty = true;
        }
    }

    void addAfter(Key<? extends T> key, Provider<? extends T> provider, Key<? extends T> after) {
        providers.put(key, provider);
        synchronized (graph) {
            graph.add(key, after);
            dirty = true;
        }
    }

    void insertBefore(Key<? extends T> key, Provider<? extends T> provider, Key<? extends T> before) {
        providers.put(key, provider);
        synchronized (graph) {
            graph.add(before, key);
            dirty = true;
        }
    }

    void addAll(Map<Key<? extends T>, Provider<? extends T>> keyProviderMap) {
        providers.putAll(keyProviderMap);
        synchronized (graph) {
            graph.addAll(keyProviderMap.keySet());
            dirty = true;
        }
    }

    void addAllAfter(Map<Key<? extends T>, Provider<? extends T>> keyProviderMap, Key<? extends T> after) {
        providers.putAll(keyProviderMap);
        synchronized (graph) {
            for (Key<? extends T> key : keyProviderMap.keySet()) {
                graph.add(key, after);
                dirty = true;
            }
        }
    }

    void insertAllBefore(Map<Key<? extends T>, Provider<? extends T>> keyProviderMap, Key<? extends T> before) {
        providers.putAll(keyProviderMap);
        synchronized (graph) {
            for (Key<? extends T> key : keyProviderMap.keySet()) {
                graph.add(before, key);
                dirty = true;
            }
        }
    }
}
