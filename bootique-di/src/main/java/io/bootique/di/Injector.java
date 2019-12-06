/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.bootique.di;

import java.util.Collection;
import javax.inject.Provider;

/**
 * A facade to the Bootique DI container. To create an injector use {@link DIBootstrap} static methods.
 */
public interface Injector {

    /**
     * Returns a service instance bound in the container for a specific type. Throws
     * {@link DIRuntimeException} if the type is not bound, or an instance can not be
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
     * @param type binding type to check
     * @return is provider for given type registered
     */
    boolean hasProvider(Class<?> type) throws DIRuntimeException;


    /**
     * @param key binding key to check
     * @return is provider for given key registered
     */
    boolean hasProvider(Key<?> key) throws DIRuntimeException;

    /**
     * Performs field injection on a given object, ignoring constructor injection. Since
     * Injector returns fully injected objects, this method is rarely used directly.
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

    /**
     * Returns collection of {@link Key} bound to given type, regardless of additional qualifiers
     * (annotations and/or names).
     *
     * @param type interested class object
     * @param <T> type
     * @return collection of keys bound to given type
     */
    <T> Collection<Key<T>> getKeysByType(Class<T> type);
}
