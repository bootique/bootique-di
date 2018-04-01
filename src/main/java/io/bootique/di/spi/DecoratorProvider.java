package io.bootique.di.spi;

import io.bootique.di.DIRuntimeException;
import javax.inject.Provider;

interface DecoratorProvider<T> {

    Provider<T> get(Provider<T> undecorated) throws DIRuntimeException;
}
