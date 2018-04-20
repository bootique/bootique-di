package io.bootique.di.spi;

import javax.inject.Provider;

/**
 * A wrapper around a provider that itself generates providers.
 */
class CustomProvidersProvider<T> implements Provider<T> {

    private Provider<Provider<? extends T>> providerOfProviders;

    CustomProvidersProvider(Provider<Provider<? extends T>> providerOfProviders) {
        this.providerOfProviders = providerOfProviders;
    }

    @Override
    public T get() {
        return providerOfProviders.get().get();
    }
}
