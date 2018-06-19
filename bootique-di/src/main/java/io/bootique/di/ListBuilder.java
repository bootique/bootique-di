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

/**
 * A binding builder for list configurations.
 * This builder supports explicit order of elements.
 *
 * @param <T> A type of list values.
 */
public interface ListBuilder<T> extends ScopeBuilder {

    ListBuilder<T> add(Class<? extends T> interfaceType) throws DIRuntimeException;

    ListBuilder<T> add(T value) throws DIRuntimeException;

    ListBuilder<T> add(Key<? extends T> valueKey) throws DIRuntimeException;

    ListBuilder<T> addAll(Collection<T> values) throws DIRuntimeException;

    ListBuilder<T> addAfter(Class<? extends T> interfaceType, Class<? extends T> afterType) throws DIRuntimeException;

    ListBuilder<T> addAfter(T value, Class<? extends T> afterType) throws DIRuntimeException;

    ListBuilder<T> addAfter(Key<? extends T> valueKey, Class<? extends T> afterType) throws DIRuntimeException;

    ListBuilder<T> addAllAfter(Collection<T> values, Class<? extends T> afterType) throws DIRuntimeException;

    ListBuilder<T> insertBefore(Class<? extends T> interfaceType, Class<? extends T> beforeType) throws DIRuntimeException;

    ListBuilder<T> insertBefore(T value, Class<? extends T> beforeType) throws DIRuntimeException;

    ListBuilder<T> insertBefore(Key<? extends T> valueKey, Class<? extends T> beforeType) throws DIRuntimeException;

    ListBuilder<T> insertAllBefore(Collection<T> values, Class<? extends T> afterType) throws DIRuntimeException;

}
