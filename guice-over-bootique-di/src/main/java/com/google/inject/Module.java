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

package com.google.inject;

/**
 * A module contributes configuration information, typically interface bindings, which will be used
 * to create an {@link Injector}. A Guice-based application is ultimately composed of little more
 * than a set of {@code Module}s and some bootstrapping code.
 *
 * <p>In addition to the bindings configured via {@link #configure}, bindings will be created for
 * all methods annotated with {@literal @}{@link Provides}. Use scope and binding annotations on
 * these methods to configure the bindings.
 */
public interface Module {

    /**
     * Contributes bindings and other configurations for this module to {@code binder}.
     */
    void configure(Binder binder);
}