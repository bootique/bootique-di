package io.bootique.di;

import java.util.Map;

/**
 * A binding builder for map configurations. Creates a parameterized map of type &lt;String, T&gt;.
 *
 * @param <T> A type of the map values.
 */
public interface MapBuilder<T> {

    MapBuilder<T> put(String key, Class<? extends T> interfaceType) throws DIRuntimeException;

    MapBuilder<T> put(String key, T value) throws DIRuntimeException;

    MapBuilder<T> putAll(Map<String, T> map) throws DIRuntimeException;

    void in(Scope scope);
}
