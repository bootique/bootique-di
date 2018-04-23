package io.bootique.di;

import java.util.Collection;

/**
 * A binding builder for list configurations.
 * This builder supports explicit order of elements.
 *
 * @param <T> A type of list values.
 */
public interface ListBuilder<T> extends ScopeBuilder {

    ListBuilder<T> add(Class<? extends T> interfaceType) throws DIRuntimeException;

    ListBuilder<T> add(T value) throws DIRuntimeException;

    ListBuilder<T> add(Key<? extends T> valueKey) throws DIRuntimeException;

    ListBuilder<T> addAll(Collection<T> values) throws DIRuntimeException;

    ListBuilder<T> addAfter(Class<? extends T> interfaceType, Class<? extends T> afterType) throws DIRuntimeException;

    ListBuilder<T> addAfter(T value, Class<? extends T> afterType) throws DIRuntimeException;

    ListBuilder<T> addAfter(Key<? extends T> valueKey, Class<? extends T> afterType) throws DIRuntimeException;

    ListBuilder<T> addAllAfter(Collection<T> values, Class<? extends T> afterType) throws DIRuntimeException;

    ListBuilder<T> insertBefore(Class<? extends T> interfaceType, Class<? extends T> beforeType) throws DIRuntimeException;

    ListBuilder<T> insertBefore(T value, Class<? extends T> beforeType) throws DIRuntimeException;

    ListBuilder<T> insertBefore(Key<? extends T> valueKey, Class<? extends T> beforeType) throws DIRuntimeException;

    ListBuilder<T> insertAllBefore(Collection<T> values, Class<? extends T> afterType) throws DIRuntimeException;

}
