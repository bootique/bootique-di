package io.bootique.di.spi;

import io.bootique.di.DIRuntimeException;

import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

class MapProvider<K, V> implements Provider<Map<K, V>> {

	private Map<K, Provider<? extends V>> providers;

	MapProvider() {
		this.providers = new HashMap<>();
	}

	@Override
	public Map<K, V> get() throws DIRuntimeException {
		Map<K, V> map = new HashMap<>();

		for (Entry<K, Provider<? extends V>> entry : providers.entrySet()) {
			map.put(entry.getKey(), entry.getValue().get());
		}

		return map;
	}

	void put(K key, Provider<? extends V> provider) {
		providers.put(key, provider);
	}
}
