package io.bootique.di.spi;

import io.bootique.di.DIRuntimeException;
import io.bootique.di.Key;
import io.bootique.di.MapBuilder;

import javax.inject.Provider;
import java.util.Map;
import java.util.Map.Entry;

class DefaultMapBuilder<T> extends DICollectionBuilder<Map<String, T>, T> implements MapBuilder<T> {

    DefaultMapBuilder(Key<Map<String, T>> bindingKey, DefaultInjector injector) {
        super(bindingKey, injector);

        // trigger initialization of the MapProvider right away, as we need to bind an
        // empty map even if the user never calls 'put'
        findOrCreateMapProvider();
    }

    @Override
    public MapBuilder<T> put(String key, Class<? extends T> interfaceType) throws DIRuntimeException {

        Provider<? extends T> provider = createTypeProvider(interfaceType);
        // TODO: andrus 11/15/2009 - report overriding the key??
        findOrCreateMapProvider().put(key, provider);
        return this;
    }

    @Override
    public MapBuilder<T> put(String key, T value) throws DIRuntimeException {
        // TODO: andrus 11/15/2009 - report overriding the key??
        findOrCreateMapProvider().put(key, createInstanceProvider(value));
        return this;
    }

    @Override
    public MapBuilder<T> putAll(Map<String, T> map) throws DIRuntimeException {

        MapProvider<T> provider = findOrCreateMapProvider();

        for (Entry<String, T> entry : map.entrySet()) {
            provider.put(entry.getKey(), createInstanceProvider(entry.getValue()));
        }

        return this;
    }

    private MapProvider<T> findOrCreateMapProvider() {
        MapProvider<T> provider;

        Binding<Map<String, T>> binding = injector.getBinding(bindingKey);
        if (binding == null) {
            provider = new MapProvider<>();
            injector.putBinding(bindingKey, provider);
        } else {
            provider = (MapProvider<T>) binding.getOriginal();
        }

        return provider;
    }
}
