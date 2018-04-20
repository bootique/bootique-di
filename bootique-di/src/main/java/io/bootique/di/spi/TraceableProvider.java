package io.bootique.di.spi;

import java.util.Objects;
import javax.inject.Provider;

import io.bootique.di.Key;

/**
 * Provider that wraps other provider to keep trace of injection.
 *
 * @param <T> type of provided object
 */
class TraceableProvider<T> implements Provider<T> {

    private final Key<T> key;
    private final Provider<T> delegate;
    private final DefaultInjector injector;

    TraceableProvider(Key<T> key, Provider<T> delegate, DefaultInjector injector) {
        this.key = Objects.requireNonNull(key);
        this.delegate = Objects.requireNonNull(delegate);
        this.injector = Objects.requireNonNull(injector);
    }

    @Override
    public T get() {
        injector.tracePush(key);
        T result = delegate.get();
        if (result == null && delegate != OptionalBindingBuilder.NULL_PROVIDER) {
            // throw early here, to trace this error with more details
            injector.throwException("Underlying provider (%s) returned NULL instance", DIUtil.getProviderName(delegate));
        }
        injector.tracePop();
        return result;
    }

    @SuppressWarnings("unchecked")
    <P extends Provider<T>> P unwrap() {
        return (P)delegate;
    }

    public Key<T> getKey() {
        return key;
    }
}
