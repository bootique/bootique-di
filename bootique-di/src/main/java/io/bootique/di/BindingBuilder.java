package io.bootique.di;

import javax.inject.Provider;

/**
 * A binding builder that helps with fluent binding creation.
 *
 * @param <T> An interface type of the service being bound.
 */
public interface BindingBuilder<T> extends ScopeBuilder {

    ScopeBuilder to(Class<? extends T> implementation) throws DIRuntimeException;

    ScopeBuilder to(Key<? extends T> key) throws DIRuntimeException;

    ScopeBuilder toInstance(T instance) throws DIRuntimeException;

    ScopeBuilder toProvider(Class<? extends Provider<? extends T>> providerType) throws DIRuntimeException;

    ScopeBuilder toProviderInstance(Provider<? extends T> provider) throws DIRuntimeException;

}
