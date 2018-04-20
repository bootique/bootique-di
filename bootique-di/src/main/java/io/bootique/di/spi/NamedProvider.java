package io.bootique.di.spi;

import javax.inject.Provider;

/**
 * Named provider.
 * Used internally by injector for better diagnostic messages.
 *
 * @param <T>  provided type
 */
public interface NamedProvider<T> extends Provider<T> {

    /**
     * @return human readable name of this provider
     */
    default String getName() {
        return this.getClass().getName();
    }

}
