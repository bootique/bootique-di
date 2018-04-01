package io.bootique.di.spi;

import javax.inject.Provider;

class InstanceProvider<T> implements Provider<T> {

    private T value;

    InstanceProvider(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }
}
