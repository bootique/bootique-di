package io.bootique.di;

import java.util.Collection;

/**
 * A binding builder for set configurations.
 *
 * @param <T> A type of set elements
 */
public interface SetBuilder<T> extends ScopeBuilder {

    SetBuilder<T> add(Class<? extends T> interfaceType) throws DIRuntimeException;

    SetBuilder<T> add(T value) throws DIRuntimeException;

    SetBuilder<T> add(Key<? extends T> valueKey) throws DIRuntimeException;

    SetBuilder<T> addAll(Collection<T> values) throws DIRuntimeException;

}
