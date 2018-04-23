package io.bootique.di.spi;

import java.util.Objects;
import javax.inject.Provider;

class InstanceProvider<T> implements Provider<T> {

    private final T value;

    InstanceProvider(T value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public T get() {
        return value;
    }
}
