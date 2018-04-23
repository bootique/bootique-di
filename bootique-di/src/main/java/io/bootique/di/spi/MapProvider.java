package io.bootique.di.spi;

import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

class MapProvider<K, V> implements Provider<Map<K, V>> {

    private final Map<K, Provider<? extends V>> providers;
    private final DefaultInjector injector;

    MapProvider(DefaultInjector injector) {
        this.providers = new ConcurrentHashMap<>();
        this.injector = injector;
    }

    @Override
    public Map<K, V> get() {
        Map<K, V> map = new HashMap<>();

        for (Entry<K, Provider<? extends V>> entry : providers.entrySet()) {
            injector.trace("Resolve map key '%s'", entry.getKey());
            map.put(entry.getKey(), entry.getValue().get());
        }

        return map;
    }

    void put(K key, Provider<? extends V> provider) {
        providers.put(key, provider);
    }
}
