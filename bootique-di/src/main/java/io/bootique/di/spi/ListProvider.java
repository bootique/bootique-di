package io.bootique.di.spi;

import io.bootique.di.DIRuntimeException;
import io.bootique.di.Key;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ListProvider<T> implements Provider<List<T>> {

    private Map<Key<? extends T>, Provider<? extends T>> providers;
    private DIGraph<Key<? extends T>> graph;

    ListProvider() {
        this.providers = new HashMap<>();
        this.graph = new DIGraph<>();
    }

    @Override
    public List<T> get() throws DIRuntimeException {
        List<Key<? extends T>> insertOrder = graph.topSort();

        if (insertOrder.size() != providers.size()) {
            List<Key<? extends T>> emptyKeys = new ArrayList<>();

            for (Key<? extends T> key : insertOrder) {
                if (!providers.containsKey(key)) {
                    emptyKeys.add(key);
                }
            }

            throw new DIRuntimeException("DI list has no providers for keys: %s", emptyKeys);
        }

        List<T> list = new ArrayList<>(insertOrder.size());
        for (Key<? extends T> key : insertOrder) {
            list.add(providers.get(key).get());
        }

        return list;
    }

    void add(Key<? extends T> key, Provider<? extends T> provider) {
        providers.put(key, provider);
        graph.add(key);
    }

    void addAfter(Key<? extends T> key, Provider<? extends T> provider, Key<? extends T> after) {
        providers.put(key, provider);
        graph.add(key, after);
    }

    void insertBefore(Key<? extends T> key, Provider<? extends T> provider, Key<? extends T> before) {
        providers.put(key, provider);
        graph.add(before, key);
    }

    void addAll(Map<Key<? extends T>, Provider<? extends T>> keyProviderMap) {
        providers.putAll(keyProviderMap);
        graph.addAll(keyProviderMap.keySet());
    }

    void addAllAfter(Map<Key<? extends T>, Provider<? extends T>> keyProviderMap, Key<? extends T> after) {
        providers.putAll(keyProviderMap);
        for (Key<? extends T> key : keyProviderMap.keySet()) {
            graph.add(key, after);
        }
    }

    void insertAllBefore(Map<Key<? extends T>, Provider<? extends T>> keyProviderMap, Key<? extends T> before) {
        providers.putAll(keyProviderMap);
        for (Key<? extends T> key : keyProviderMap.keySet()) {
            graph.add(before, key);
        }
    }
}
