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

package io.bootique.di.spi;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.inject.Provider;

import io.bootique.di.Key;

class SetProvider<T> implements Provider<Set<T>> {

    private final DefaultInjector injector;
    private final Collection<Provider<? extends T>> providers;
    private final Key<Set<T>> bindingKey;

    SetProvider(DefaultInjector injector, Key<Set<T>> bindingKey) {
        this.injector = injector;
        this.providers = new ConcurrentLinkedQueue<>();
        this.bindingKey = bindingKey;
    }

    @Override
    public Set<T> get() {
        Set<T> set = new HashSet<>(providers.size());
        int i = 0;
        for (Provider<? extends T> provider : providers) {
            int idx = i++;
            injector.trace(() -> "Resolving set element " + idx);
            T value = provider.get();
            if (!set.add(value)) {
                injector.throwException("Found duplicated value '%s' in set %s.", value, bindingKey);
            }
        }

        return set;
    }

    void add(Provider<? extends T> provider) {
        providers.add(provider);
    }

}
