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

import com.google.inject.Key;
import com.google.inject.binder.LinkedBindingBuilder;

public class OptionalBinderAdapter<T> {

    private final BinderAdapter guiceBinderAdapter;
    private final OptionalBindingBuilderAdapter<T> adapter;
    private final Key<T> key;

    public OptionalBinderAdapter(com.google.inject.Binder guiceBinder, Key<T> key) {
        this.key = key;
        this.guiceBinderAdapter = (BinderAdapter) guiceBinder;
        this.adapter = createBindingBuilder();
    }

    public LinkedBindingBuilder<T> setDefault() {
        return adapter;
    }

    public LinkedBindingBuilder<T> setBinding() {
        return adapter;
    }

    private OptionalBindingBuilderAdapter<T> createBindingBuilder() {
        OptionalBindingBuilderAdapter<T> adapter = new OptionalBindingBuilderAdapter<>(
                guiceBinderAdapter, ConversionUtils.toBootiqueKey(key));
        guiceBinderAdapter.getInjectorAdapter().registerBindingBuilder(adapter);
        return adapter;
    }
}
