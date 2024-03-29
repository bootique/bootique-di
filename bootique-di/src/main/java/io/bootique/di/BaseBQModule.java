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

/**
 * A common superclass of modules with an empty implementation of {@link #configure(Binder)}. It may come handy as modules
 * may bind services declaratively by creating methods annotated by {@link Provides @Provides} and don't need to
 * implement 'configure'.
 *
 * @deprecated unused. If you inherited from it, just implement {@link BQModule} directly or subclass one of the
 * Bootique core modules like BaseModule.
 */
@Deprecated(since = "3.0", forRemoval = true)
public abstract class BaseBQModule implements BQModule {

    /**
     * An empty implementation of the Module contract.
     *
     * @param binder a binder object passed by the injector assembly environment.
     */
    @Override
    public void configure(Binder binder) {
    }
}
