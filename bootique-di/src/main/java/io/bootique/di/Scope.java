package io.bootique.di;

import javax.inject.Provider;

/**
 * Defines the scope of the instances created by the DI container. I.e. whether instances
 * are shared between the callers, and for how longs or whether they are created anew.
 * Scope object is also used to tie DI-produced instances to the Injector events, such as
 * shutdown. Default scope in Bootique DI is "singleton".
 */
public interface Scope {

    <T> Provider<T> scope(Provider<T> unscoped);
}
