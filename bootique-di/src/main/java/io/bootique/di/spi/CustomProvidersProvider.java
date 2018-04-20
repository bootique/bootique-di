package io.bootique.di.spi;

import javax.inject.Provider;

/**
 * A wrapper around a provider that itself generates providers.
 */
class CustomProvidersProvider<T> implements NamedProvider<T> {

    private DefaultInjector injector;
    private Class<? extends Provider<? extends T>> providerType;
    private Provider<Provider<? extends T>> providerOfProviders;

    CustomProvidersProvider(DefaultInjector injector, Class<? extends Provider<? extends T>> providerType, Provider<Provider<? extends T>> providerOfProviders) {
        this.injector = injector;
        this.providerType = providerType;
        this.providerOfProviders = providerOfProviders;
    }

    @Override
    public T get() {
        Provider<? extends T> customProvider = providerOfProviders.get();
        injector.trace("Invoking " + getName());
        return customProvider.get();
    }

    @Override
    public String getName() {
        return "custom provider of type " + providerType.getName();
    }
}
