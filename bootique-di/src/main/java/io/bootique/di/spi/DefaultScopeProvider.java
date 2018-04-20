package io.bootique.di.spi;

import io.bootique.di.DIRuntimeException;

import javax.inject.Provider;

/**
 * A provider that provides scoping for other providers.
 */
public class DefaultScopeProvider<T> implements Provider<T> {

    private Provider<T> delegate;
    private DefaultScope scope;

    // presumably "volatile" works in Java 5 and newer to prevent double-checked locking
    private volatile T instance;

    public DefaultScopeProvider(DefaultScope scope, Provider<T> delegate) {
        this.scope = scope;
        this.delegate = delegate;

        scope.addScopeEventListener(this);
    }

    @Override
    public T get() {
        T localInstance = instance;
        if (localInstance == null) {
            synchronized (this) {
                localInstance = instance;
                if (localInstance == null) {
                    localInstance = instance = delegate.get();
                    if (localInstance == null) {
                        // TODO: can we use injector.throwException() here?
                        throw new DIRuntimeException("Underlying provider (%s) returned NULL instance"
                                , DIUtil.getProviderName(delegate));
                    }

                    scope.addScopeEventListener(localInstance);
                }
            }
        }

        return localInstance;
    }

    @AfterScopeEnd
    public void afterScopeEnd() throws Exception {
        Object localInstance = instance;

        if (localInstance != null) {
            instance = null;
            scope.removeScopeEventListener(localInstance);
        }
    }
}
