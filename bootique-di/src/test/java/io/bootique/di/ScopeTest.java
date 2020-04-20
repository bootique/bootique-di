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

import org.junit.Test;

import javax.inject.Singleton;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class ScopeTest {

    @Test
    public void testImplicitScope() {
        Injector injector = DIBootstrap
                .injectorBuilder(binder -> binder.bind(TI.class).to(TC.class))
                .build();
        assertSame(injector.getInstance(TI.class), injector.getInstance(TI.class));
    }

    @Test
    public void testImplicitScope_WithoutScope() {
        Injector injector = DIBootstrap
                .injectorBuilder(binder -> binder.bind(TI.class).to(TC.class).withoutScope())
                .build();
        assertNotSame(injector.getInstance(TI.class), injector.getInstance(TI.class));
    }

    @Test
    public void testDefaultNoScope() {
        Injector injector = DIBootstrap
                .injectorBuilder(binder -> binder.bind(TI.class).to(TC.class))
                .defaultNoScope()
                .build();
        assertNotSame(injector.getInstance(TI.class), injector.getInstance(TI.class));
    }

    @Test
    public void testDefaultNoScope_SingletonAnnotation() {
        Injector injector = DIBootstrap
                .injectorBuilder(binder -> binder.bind(TI.class).to(TCSingleton.class))
                .defaultNoScope()
                .build();
        assertSame(injector.getInstance(TI.class), injector.getInstance(TI.class));
    }

    public interface TI {

    }

    public static class TC implements TI {

    }

    @Singleton
    public static class TCSingleton implements TI {

    }
}
