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

package com.google.inject;

/**
 * A mapping from a key (type and optional annotation) to the strategy for getting instances of the
 * type. This interface is part of the introspection API and is intended primarily for use by tools.
 *
 * <p>Bindings are created in several ways:
 *
 * <ul>
 * <li>Explicitly in a module, via {@code bind()} and {@code bindConstant()} statements:
 * <pre>
 *     bind(Service.class).annotatedWith(Red.class).to(ServiceImpl.class);
 *     bindConstant().annotatedWith(ServerHost.class).to(args[0]);</pre>
 *
 * <li>By converting a bound instance to a different type.
 * <li>For {@link Provider providers}, by delegating to the binding for the provided type.
 * </ul>
 *
 * <p>They exist on both modules and on injectors, and their behaviour is different for each:
 *
 * <ul>
 * <li><strong>Module bindings</strong> are incomplete and cannot be used to provide instances. This
 * is because the applicable scopes and interceptors may not be known until an injector is
 * created. From a tool's perspective, module bindings are like the injector's source code. They
 * can be inspected or rewritten, but this analysis must be done statically.
 * <li><strong>Injector bindings</strong> are complete and valid and can be used to provide
 * instances. From a tools' perspective, injector bindings are like reflection for an injector.
 * They have full runtime information, including the complete graph of injections necessary to
 * satisfy a binding.
 * </ul>
 *
 * @param <T> the bound type. The injected is always assignable to this type.
 * @author crazybob@google.com (Bob Lee)
 * @author jessewilson@google.com (Jesse Wilson)
 */
public interface Binding<T> {

    /**
     * Returns the key for this binding.
     */
    Key<T> getKey();

    /**
     * Returns the scoped provider guice uses to fulfill requests for this binding.
     *
     * @throws UnsupportedOperationException when invoked on a {@link Binding} created via
     *                                       com.google.inject.spi.Elements#getElements. This method is only supported on {@link
     *                                       Binding}s returned from an injector.
     */
    Provider<T> getProvider();

}
