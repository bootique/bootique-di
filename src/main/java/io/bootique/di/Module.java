
package io.bootique.di;

/**
 * Represents a unit of configuration of the Cayenne DI container.
 * 
 * @since 3.1
 */
public interface Module {

    void configure(Binder binder);
}
