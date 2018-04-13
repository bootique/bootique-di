package io.bootique.di;

import io.bootique.di.spi.InjectorPredicates;
import io.bootique.di.spi.DefaultInjector;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.inject.Provider;

/**
 * A class that bootstraps the Bootique DI container.
 */
public class DIBootstrap {

    /**
     * Creates injector builder.
     * @return builder
     */
    public static InjectorBuilder injectorBuilder() {
        return new InjectorBuilder();
    }

    /**
     * Creates injector builder.
     * @return builder
     */
    public static InjectorBuilder injectorBuilder(Module... modules) {
        return new InjectorBuilder(modules);
    }

    /**
     * Creates injector builder.
     * @return builder
     */
    public static InjectorBuilder injectorBuilder(Collection<Module> modules) {
        return injectorBuilder(modules.toArray(new Module[0]));
    }

    /**
     * Creates and returns an injector instance working with the set of provided modules.
     * Shortcut for injectorBuilder(modules).build()
     * @return injector with default configuration
     */
    public static Injector createInjector(Module... modules) throws DIRuntimeException {
        return injectorBuilder(modules).build();
    }

    /**
     * Creates and returns an injector instance working with the set of provided modules.
     * Shortcut for injectorBuilder(modules).build()
     * @return injector with default configuration
     */
    public static Injector createInjector(Collection<Module> modules) {
        return injectorBuilder(modules).build();
    }

    /**
     * Injector builder that allows to configure injector
     */
    public static class InjectorBuilder {
        private Set<DefaultInjector.Options> options;
        private InjectorPredicates annotationPredicates;
        private Module[] modules;

        private InjectorBuilder(Module... modules) {
            this.options = Collections.newSetFromMap(new EnumMap<>(DefaultInjector.Options.class));
            this.modules = modules;
            this.annotationPredicates = new InjectorPredicates();
        }

        /**
         * Enable dynamic (i.e. not registered directly in binder) binding resolution.
         * Disabled by default, injector will throw in case of unknown binding.
         *
         * @return this
         */
        public InjectorBuilder enableDynamicBindings() {
            this.options.add(DefaultInjector.Options.ENABLE_DYNAMIC_BINDINGS);
            return this;
        }

        /**
         * Allow only declared overrides.
         * Disabled by default, all overrides allowed.
         *
         * @return this
         */
        public InjectorBuilder declaredOverridesOnly() {
            this.options.add(DefaultInjector.Options.DECLARED_OVERRIDE_ONLY);
            return this;
        }

        /**
         * Create unscoped bindings by default, otherwise singleton scope will be used.
         *
         * @return this
         */
        public InjectorBuilder defaultNoScope() {
            this.options.add(DefaultInjector.Options.NO_SCOPE_BY_DEFAULT);
            return this;
        }

        /**
         * Enable injection into methods.
         * Disabled by default.
         *
         * @return this
         */
        public InjectorBuilder enableMethodInjection() {
            this.options.add(DefaultInjector.Options.ENABLE_METHOD_INJECTION);
            return this;
        }

        /**
         * Set custom predicate for methods in modules that should be used as providers.
         * Default predicate test methods for {@link io.bootique.di.Provides} annotation.
         *
         * @param providesMethodPredicate method predicate
         * @return this
         */
        public InjectorBuilder withProvidesMethodPredicate(Predicate<Method> providesMethodPredicate) {
            annotationPredicates.setProvidesMethodPredicate(providesMethodPredicate);
            return this;
        }

        /**
         * Set custom inject predicate.
         * Default predicate test constructors, methods and fields for {@link javax.inject.Inject} annotation.
         *
         * @param injectPredicate inject predicate
         * @return this
         */
        public InjectorBuilder withInjectAnnotationPredicate(Predicate<AccessibleObject> injectPredicate) {
            annotationPredicates.setInjectPredicate(injectPredicate);
            return this;
        }

        /**
         * Set custom predicate for Provider type.
         * By default {@link javax.inject.Provider} class is used.
         *
         * @param providerPredicate provider type predicate
         * @return this
         */
        public InjectorBuilder withProviderPredicate(Predicate<Type> providerPredicate) {
            annotationPredicates.setProviderPredicate(providerPredicate);
            return this;
        }

        /**
         * Set custom predicate for qualifying annotations.
         * By default tests for {@link javax.inject.Qualifier} annotation.
         *
         * @param qualifierPredicate qualifier predicate
         * @return this
         */
        public InjectorBuilder withQualifierPredicate(Predicate<Class<? extends Annotation>> qualifierPredicate) {
            annotationPredicates.setQualifierPredicate(qualifierPredicate);
            return this;
        }

        /**
         * Set custom singleton scope predicate.
         * By default tests for {@link javax.inject.Singleton} annotation.
         *
         * @param singletonPredicate singleton predicate
         * @return this
         */
        public InjectorBuilder withSingletonPredicate(Predicate<AnnotatedElement> singletonPredicate) {
            annotationPredicates.setSingletonPredicate(singletonPredicate);
            return this;
        }

        /**
         * Set custom provider implementation.
         * By default {@link javax.inject.Provider} used as is.
         *
         * @param providerFunction provider wrapping function
         * @return this
         */
        public <T> InjectorBuilder withProviderWrapper(Function<Provider<T>, Provider<T>> providerFunction) {
            annotationPredicates.setProviderFunction(providerFunction);
            return this;
        }

        /**
         * Build injector with provided options.
         *
         * @return injector
         */
        public Injector build() {
            return new DefaultInjector(options, annotationPredicates, modules);
        }
    }

}
