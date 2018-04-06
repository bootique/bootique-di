package io.bootique.di.spi;

import io.bootique.di.DIRuntimeException;

import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

class MapProvider<T> implements Provider<Map<String, T>> {

	private Map<String, Provider<? extends T>> providers;

	MapProvider() {
		this.providers = new HashMap<>();
	}

	@Override
	public Map<String, T> get() throws DIRuntimeException {
		Map<String, T> map = new HashMap<>();

		for (Entry<String, Provider<? extends T>> entry : providers.entrySet()) {
			map.put(entry.getKey(), entry.getValue().get());
		}

		return map;
	}

	void put(String key, Provider<? extends T> provider) {
		providers.put(key, provider);
	}
}
