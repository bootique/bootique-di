package io.bootique.di;

import io.bootique.di.spi.DefaultInjector;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A class that bootstraps the Bootique DI container.
 */
public class DIBootstrap {

    public static InjectorBuilder injectorBuilder() {
        return new InjectorBuilder();
    }

    public static InjectorBuilder injectorBuilder(Module... modules) {
        return new InjectorBuilder(modules);
    }

    public static InjectorBuilder injectorBuilder(Collection<Module> modules) {
        return injectorBuilder(modules.toArray(new Module[0]));
    }

    /**
     * Creates and returns an injector instance working with the set of provided modules.
     */
    public static Injector createInjector(Module... modules) throws DIRuntimeException {
        return new DefaultInjector(Collections.emptySet(), modules);
    }

    /**
     * Creates and returns an injector instance working with the set of provided modules.
     */
    public static Injector createInjector(Collection<Module> modules) {
        Module[] moduleArray = modules.toArray(new Module[0]);
        return createInjector(moduleArray);
    }

    public static class InjectorBuilder {
        private Set<DefaultInjector.Options> options;
        private Module[] modules;

        InjectorBuilder(Module... modules) {
            this.options = new HashSet<>();
            this.modules = modules;
        }

        public InjectorBuilder enableDynamicBindings() {
            this.options.add(DefaultInjector.Options.ENABLE_DYNAMIC_BINDINGS);
            return this;
        }

        public InjectorBuilder declaredOverridesOnly() {
            this.options.add(DefaultInjector.Options.DECLARED_OVERRIDE_ONLY);
            return this;
        }

        public InjectorBuilder defaultNoScope() {
            this.options.add(DefaultInjector.Options.NO_SCOPE_BY_DEFAULT);
            return this;
        }

        public InjectorBuilder enableMethodInjection() {
            this.options.add(DefaultInjector.Options.ENABLE_METHOD_INJECTION);
            return this;
        }

        public Injector build() {
            return new DefaultInjector(options, modules);
        }
    }

}
