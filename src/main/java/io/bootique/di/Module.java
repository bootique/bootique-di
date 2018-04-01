
package io.bootique.di;

/**
 * Represents a unit of configuration of the Cayenne DI container.
 */
public interface Module {

    void configure(Binder binder);
}
