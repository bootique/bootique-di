
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

        if (instance == null) {
            synchronized (this) {
                if (instance == null) {
                    instance = delegate.get();

                    if (instance == null) {
                        throw new DIRuntimeException(
                                "Underlying provider (%s) returned NULL instance",
                                delegate.getClass().getName());
                    }

                    scope.addScopeEventListener(instance);
                }
            }
        }

        return instance;
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
