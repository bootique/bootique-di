package io.bootique.di;

/**
 * Represents a unit of configuration of the Bootique DI container.
 */
public interface Module {

    void configure(Binder binder);
}
