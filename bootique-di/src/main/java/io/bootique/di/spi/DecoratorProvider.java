package io.bootique.di.spi;

import javax.inject.Provider;

interface DecoratorProvider<T> {

    Provider<T> get(Provider<T> undecorated);

}
