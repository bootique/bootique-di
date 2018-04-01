package io.bootique.di;

import io.bootique.di.spi.DefaultInjector;

import java.util.Collection;

/**
 * A class that bootstraps the Bootique DI container.
 */
public class DIBootstrap {

    /**
     * Creates and returns an injector instance working with the set of provided modules.
     */
    public static Injector createInjector(Module... modules) throws DIRuntimeException {
        return new DefaultInjector(modules);
    }

    /**
     * Creates and returns an injector instance working with the set of provided modules.
     */
    public static Injector createInjector(Collection<Module> modules) {
        Module[] moduleArray = modules.toArray(new Module[modules.size()]);
        return createInjector(moduleArray);
    }
}
