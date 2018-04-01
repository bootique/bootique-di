
package io.bootique.di;

import java.util.Collection;

import io.bootique.di.spi.DefaultInjector;

/**
 * A class that bootstraps the Cayenne DI container.
 */
public class DIBootstrap {

    /**
     * Creates and returns an injector instance working with the set of provided modules.
     */
    public static Injector createInjector(Module... modules)
            throws DIRuntimeException {
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
