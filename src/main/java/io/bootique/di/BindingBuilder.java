package io.bootique.di;

import javax.inject.Provider;

/**
 * A binding builder that helps with fluent binding creation.
 *
 * @param <T> An interface type of the service being bound.
 */
public interface BindingBuilder<T> {

    BindingBuilder<T> to(Class<? extends T> implementation) throws DIRuntimeException;

    BindingBuilder<T> toInstance(T instance) throws DIRuntimeException;

    BindingBuilder<T> toProvider(Class<? extends Provider<? extends T>> providerType)
            throws DIRuntimeException;

    BindingBuilder<T> toProviderInstance(Provider<? extends T> provider)
            throws DIRuntimeException;

    /**
     * Sets the scope of a bound instance. This method is used to change the default scope
     * which is usually a singleton to a custom scope.
     */
    void in(Scope scope);

    /**
     * Sets the scope of a bound instance to singleton. Singleton is normally the default,
     * so calling this method explicitly is rarely needed.
     */
    void inSingletonScope();

    /**
     * Sets the scope of a bound instance to "no scope". This means that a new instance of
     * an object will be created on every call to {@link Injector#getInstance(Class)}.
     */
    void withoutScope();
}
