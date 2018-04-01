package io.bootique.di.spi;

import io.bootique.di.Scope;

import javax.inject.Provider;

final class NoScope implements Scope {

    static final Scope INSTANCE = new NoScope();

    @Override
    public <T> Provider<T> scope(Provider<T> unscoped) {
        return unscoped;
    }
}
