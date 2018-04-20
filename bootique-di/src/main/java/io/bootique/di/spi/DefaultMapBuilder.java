package io.bootique.di.spi;

import io.bootique.di.Key;
import io.bootique.di.MapBuilder;

import javax.inject.Provider;
import java.util.Map;
import java.util.Map.Entry;

// TODO: current implementation does nothing in case of overriding the key
class DefaultMapBuilder<K, V> extends DICollectionBuilder<Map<K, V>, V> implements MapBuilder<K, V> {

    DefaultMapBuilder(Key<Map<K, V>> bindingKey, DefaultInjector injector) {
        super(bindingKey, injector);

        // trigger initialization of the MapProvider right away, as we need to bind an
        // empty map even if the user never calls 'put'
        findOrCreateMapProvider();
    }

    @Override
    public MapBuilder<K, V> put(K key, Class<? extends V> interfaceType) {
        Provider<? extends V> provider = createTypeProvider(interfaceType);
        findOrCreateMapProvider().put(key, provider);
        return this;
    }

    @Override
    public MapBuilder<K, V> put(K key, V value) {
        findOrCreateMapProvider().put(key, createInstanceProvider(value));
        return this;
    }

    @Override
    public MapBuilder<K, V> put(K key, Key<? extends V> valueKey) {
        findOrCreateMapProvider().put(key, getByKeyProvider(valueKey));
        return this;
    }

    @Override
    public MapBuilder<K, V> putAll(Map<K, V> map) {

        MapProvider<K, V> provider = findOrCreateMapProvider();

        for (Entry<K, V> entry : map.entrySet()) {
            provider.put(entry.getKey(), createInstanceProvider(entry.getValue()));
        }

        return this;
    }

    private MapProvider<K, V> findOrCreateMapProvider() {
        MapProvider<K, V> provider;

        Binding<Map<K, V>> binding = injector.getBinding(bindingKey);
        if (binding == null) {
            provider = new MapProvider<>(injector);
            injector.putBinding(bindingKey, provider);
        } else {
            if (injector.isInjectionTraceEnabled()) {
                provider = ((TraceableProvider<Map<K, V>>) binding.getOriginal()).unwrap();
            } else {
                provider = (MapProvider<K, V>) binding.getOriginal();
            }
        }

        return provider;
    }
}
