package io.bootique.di;

/**
 * Represents a unit of configuration of the Bootique DI container.
 */
@FunctionalInterface
public interface Module {

    /**
     * A callback method invoked during injector assembly that allows the module to load its services.
     *
     * @param binder a binder object passed by the injector assembly environment.
     */
    void configure(Binder binder);
}
