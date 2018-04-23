package io.bootique.di;

import java.util.Collection;

public interface SetBuilder<T> {

    SetBuilder<T> add(Class<? extends T> interfaceType) throws DIRuntimeException;

    SetBuilder<T> add(T value) throws DIRuntimeException;

    SetBuilder<T> add(Key<? extends T> valueKey) throws DIRuntimeException;

    SetBuilder<T> addAll(Collection<T> values) throws DIRuntimeException;

    void in(Scope scope);

    void inSingleton();

    void withoutScope();
}
