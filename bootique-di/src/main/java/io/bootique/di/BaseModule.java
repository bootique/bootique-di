package io.bootique.di;

/**
 * A common superclass of modules with an empty implementation of {@link #configure(Binder)}. It may come handy as modules
 * may bind services declaratively by creating methods annotated by {@link Provides @Provides} and don't need to
 * implement 'configure'.
 */
public abstract class BaseModule implements Module {

    /**
     * An empty implementation of the Module contract.
     *
     * @param binder a binder object passed by the injector assembly environment.
     */
    @Override
    public void configure(Binder binder) {
    }
}
