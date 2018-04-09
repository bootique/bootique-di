package io.bootique.di;

import java.util.Map;

/**
 * A binding builder for map configurations. Creates a parameterized map of type &lt;K, V&gt;.
 *
 * @param <K> A type of the map keys.
 * @param <V> A type of the map values.
 */
public interface MapBuilder<K, V> {

    MapBuilder<K, V> put(K key, Class<? extends V> interfaceType) throws DIRuntimeException;

    MapBuilder<K, V> put(K key, V value) throws DIRuntimeException;

    MapBuilder<K, V> put(K key, Key<? extends V> valueKey) throws DIRuntimeException;

    MapBuilder<K, V> putAll(Map<K, V> map) throws DIRuntimeException;

    void in(Scope scope);
}
