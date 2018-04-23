package io.bootique.di;

public interface ScopeBuilder {

    /**
     * Sets the scope of a bound instance. This method is used to change the default scope
     * which is a singleton by default to a custom scope.
     */
    void in(Scope scope);

    /**
     * Sets the scope of a bound instance to singleton. Singleton is normally the default.
     */
    void inSingletonScope();

    /**
     * Sets the scope of a bound instance to "no scope". This means that a new instance of
     * an object will be created on every call to {@link Injector#getInstance(Class)} or
     * to {@link javax.inject.Provider} of this instance.
     */
    void withoutScope();

}
