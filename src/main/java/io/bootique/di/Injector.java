package io.bootique.di;

import javax.inject.Provider;

/**
 * A facade to the Cayenne DI container. To create an injector use {@link DIBootstrap}
 * static methods.
 */
public interface Injector {

    /**
     * Returns a service instance bound in the container for a specific type. Throws
     *{@link DIRuntimeException} if the type is not bound, or an instance can not be
     * created.
     */
    <T> T getInstance(Class<T> type) throws DIRuntimeException;

    /**
     * Returns a service instance bound in the container for a specific binding key.
     * Throws {@link DIRuntimeException} if the key is not bound, or an instance can
     * not be created.
     */
    <T> T getInstance(Key<T> key) throws DIRuntimeException;

    <T> Provider<T> getProvider(Class<T> type) throws DIRuntimeException;

    <T> Provider<T> getProvider(Key<T> key) throws DIRuntimeException;

    /**
     * Performs field injection on a given object, ignoring constructor injection. Since
     * Cayenne DI injector returns fully injected objects, this method is rarely used
     * directly.
     * <p>
     * Note that using this method inside a custom DI {@link Provider} will most likely
     * result in double injection, as custom provider is wrapped in a field-injecting
     * provider by the DI container. Instead custom providers must initialize object
     * properties manually, obtaining dependencies from Injector.
     */
    void injectMembers(Object object);

    /**
     * A lifecycle method that let's the injector's services to clean up their state and
     * release resources. This method would normally generate a scope end event for the
     * injector's one and only singleton scope.
     */
    void shutdown();
}
