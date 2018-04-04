/*
 * Copyright (C) 2006 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.inject;

/**
 * Builds the graphs of objects that make up your application. The injector tracks the dependencies
 * for each type and uses bindings to inject them. This is the core of Guice, although you rarely
 * interact with it directly. This "behind-the-scenes" operation is what distinguishes dependency
 * injection from its cousin, the service locator pattern.
 *
 * <p>Contains several default bindings:
 *
 * <ul>
 * <li>This {@link Injector} instance itself
 * <li>A {@code Provider<T>} for each binding of type {@code T}
 * <li>The {@link java.util.logging.Logger} for the class being injected
 * </ul>
 * <p>
 * Injectors are created using the facade class {@link Guice}.
 *
 * <p>An injector can also {@link #injectMembers(Object) inject the dependencies} of
 * already-constructed instances. This can be used to interoperate with objects created by other
 * frameworks or services.
 *
 * @author crazybob@google.com (Bob Lee)
 * @author jessewilson@google.com (Jesse Wilson)
 */
public interface Injector {

    /**
     * Injects dependencies into the fields and methods of {@code instance}. Ignores the presence or
     * absence of an injectable constructor.
     *
     * <p>Whenever Guice creates an instance, it performs this injection automatically (after first
     * performing constructor injection), so if you're able to let Guice create all your objects for
     * you, you'll never need to use this method.
     *
     * @param instance to inject members on
     */
    void injectMembers(Object instance);

    /**
     * Returns the provider used to obtain instances for the given injection key. When feasible, avoid
     * using this method, in favor of having Guice inject your dependencies ahead of time.
     *
     * @throws ConfigurationException if this injector cannot find or create the provider.
     */
    <T> Provider<T> getProvider(Key<T> key);

    /**
     * Returns the provider used to obtain instances for the given type. When feasible, avoid using
     * this method, in favor of having Guice inject your dependencies ahead of time.
     *
     * @throws ConfigurationException if this injector cannot find or create the provider.
     */
    <T> Provider<T> getProvider(Class<T> type);

    /**
     * Returns the appropriate instance for the given injection key; equivalent to {@code
     * getProvider(key).get()}. When feasible, avoid using this method, in favor of having Guice
     * inject your dependencies ahead of time.
     *
     * @throws ConfigurationException if this injector cannot find or create the provider.
     * @throws ProvisionException     if there was a runtime failure while providing an instance.
     */
    <T> T getInstance(Key<T> key);

    /**
     * Returns the appropriate instance for the given injection type; equivalent to {@code
     * getProvider(type).get()}. When feasible, avoid using this method, in favor of having Guice
     * inject your dependencies ahead of time.
     *
     * @throws ConfigurationException if this injector cannot find or create the provider.
     * @throws ProvisionException     if there was a runtime failure while providing an instance.
     */
    <T> T getInstance(Class<T> type);

    /**
     * Returns the binding if it already exists, or null if does not exist.
     * Does not attempt to create just-in-time bindings for keys that aren't bound.
     *
     * <p>This method is part of the Guice SPI and is intended for use by tools and extensions.
     *
     * @since 3.0
     */
    <T> Binding<T> getExistingBinding(Key<T> key);
}
