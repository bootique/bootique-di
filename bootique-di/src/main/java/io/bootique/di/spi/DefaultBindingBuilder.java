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

import io.bootique.di.BindingBuilder;
import io.bootique.di.Key;
import io.bootique.di.Scope;

import javax.inject.Provider;

class DefaultBindingBuilder<T> implements BindingBuilder<T> {

    protected final DefaultInjector injector;
    protected final Key<T> bindingKey;

    DefaultBindingBuilder(Key<T> bindingKey, DefaultInjector injector) {
        this.injector = injector;
        this.bindingKey = bindingKey;
        initBinding();
    }

    protected void initBinding() {
        // Put binding without provider.
        // If no provider will be configured in this builder, it will be created at resolve time by Injector.
        injector.putBinding(bindingKey, (Provider<T>) null);
    }

    protected void addBinding(Provider<T> provider) {
        injector.putBinding(bindingKey, provider);
    }

    @Override
    public BindingBuilder<T> to(Class<? extends T> implementation) {
        Provider<T> provider0 = new ConstructorInjectingProvider<>(implementation, injector);
        Provider<T> provider1 = new FieldInjectingProvider<>(provider0, injector);
        if(injector.isMethodInjectionEnabled()) {
            provider1 = new MethodInjectingProvider<>(provider1, injector);
        }

        addBinding(provider1);
        if(injector.getPredicates().isSingleton(implementation)) {
            inSingletonScope();
        }

        return this;
    }

    @Override
    public BindingBuilder<T> to(Key<? extends T> key) {
        return toProviderInstance(() -> injector.getProvider(key).get());
    }

    @Override
    public BindingBuilder<T> toInstance(T instance) {
        Provider<T> provider0 = new InstanceProvider<>(instance);
        Provider<T> provider1 = new FieldInjectingProvider<>(provider0, injector);
        if(injector.isMethodInjectionEnabled()) {
            provider1 = new MethodInjectingProvider<>(provider1, injector);
        }

        addBinding(provider1);

        return this;
    }

    @Override
    public BindingBuilder<T> toProvider(Class<? extends Provider<? extends T>> providerType) {
        Provider<Provider<? extends T>> provider0 = new ConstructorInjectingProvider<>(providerType, injector);
        Provider<Provider<? extends T>> provider1 = new FieldInjectingProvider<>(provider0, injector);
        if(injector.isMethodInjectionEnabled()) {
            provider1 = new MethodInjectingProvider<>(provider1, injector);
        }

        Provider<T> provider3 = new CustomProvidersProvider<>(injector, providerType, provider1);
        Provider<T> provider4 = new FieldInjectingProvider<>(provider3, injector);
        if(injector.isMethodInjectionEnabled()) {
            provider4 = new MethodInjectingProvider<>(provider4, injector);
        }

        addBinding(provider4);
        return this;
    }

    @Override
    public BindingBuilder<T> toProviderInstance(Provider<? extends T> provider) {
        Provider<Provider<? extends T>> provider0 = new InstanceProvider<>(provider);
        Provider<Provider<? extends T>> provider1 = new FieldInjectingProvider<>(provider0, injector);
        if(injector.isMethodInjectionEnabled()) {
            provider1 = new MethodInjectingProvider<>(provider1, injector);
        }

        @SuppressWarnings("unchecked")
        Class<? extends Provider<? extends T>> providerType = (Class<? extends Provider<? extends T>>)provider1.getClass();
        Provider<T> provider3 = new CustomProvidersProvider<>(injector, providerType, provider1);
        Provider<T> provider4 = new FieldInjectingProvider<>(provider3, injector);
        if(injector.isMethodInjectionEnabled()) {
            provider4 = new MethodInjectingProvider<>(provider4, injector);
        }

        addBinding(provider4);
        return this;
    }

    @Override
    public void in(Scope scope) {
        injector.changeBindingScope(bindingKey, scope);
    }

    @Override
    public void withoutScope() {
        in(injector.getNoScope());
    }

    @Override
    public void inSingletonScope() {
        in(injector.getSingletonScope());
    }

}
