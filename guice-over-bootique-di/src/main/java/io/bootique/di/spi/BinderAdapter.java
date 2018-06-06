/**
 *    Licensed to the ObjectStyle LLC under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ObjectStyle LLC licenses
 *  this file to you under the Apache License, Version 2.0 (the
 *  “License”); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.bootique.di.spi;

import java.util.ArrayList;
import java.util.Collection;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;

public class BinderAdapter implements Binder {

    private final io.bootique.di.Binder bootiqueBinder;

    private final InjectorAdapter injectorAdapter;

    private final Collection<BindingBuilderAdapter<?>> partialAdapters;

    BinderAdapter(io.bootique.di.Binder bootiqueBinder, InjectorAdapter injectorAdapter) {
        this.bootiqueBinder = bootiqueBinder;
        this.injectorAdapter = injectorAdapter;
        this.partialAdapters = new ArrayList<>();
    }

    @Override
    public <T> LinkedBindingBuilder<T> bind(Key<T> key) {
        return createAdapter(ConversionUtils.toBootiqueKey(key));
    }

    @Override
    public <T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral) {
        return createAdapter(ConversionUtils.toBootiqueKey(typeLiteral));
    }

    @Override
    public <T> AnnotatedBindingBuilder<T> bind(Class<T> type) {
        return createAdapter(io.bootique.di.Key.get(type));
    }

    io.bootique.di.Binder getBootiqueBinder() {
        return bootiqueBinder;
    }

    public InjectorAdapter getInjectorAdapter() {
        return injectorAdapter;
    }

    <T> BindingBuilderAdapter<T> createAdapter(io.bootique.di.Key<T> bootiqueKey) {
        BindingBuilderAdapter<T> adapter = new BindingBuilderAdapter<>(this, bootiqueKey);
        injectorAdapter.registerBindingBuilder(adapter);
        return adapter;
    }

    /**
     * Create all bindings that weren't created explicitly,
     * i.e. were bind like <pre>{@code binder.bind(Service.class);}</pre>
     */
    void finalizeBind() {
        partialAdapters.forEach(BindingBuilderAdapter::getBinding);
    }
}
