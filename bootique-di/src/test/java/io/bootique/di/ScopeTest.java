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
    public void testDefaultSingletonScope() {
        Injector injector = DIBootstrap
                .injectorBuilder(binder -> binder.bind(TI.class).to(TC.class))
                .defaultSingletonScope()
                .build();
        assertSame(injector.getInstance(TI.class), injector.getInstance(TI.class));
    }

    @Test
    public void testDefaultSingletonScope_WithoutScope() {
        Injector injector = DIBootstrap
                .injectorBuilder(binder -> binder.bind(TI.class).to(TC.class).withoutScope())
                .defaultSingletonScope()
                .build();
        assertNotSame(injector.getInstance(TI.class), injector.getInstance(TI.class));
    }

    @Test
    public void testDefaultSingletonScope_NoScopeMethod() {
        Injector injector = DIBootstrap
                .injectorBuilder(new DefaultModule())
                .defaultSingletonScope()
                .build();
        assertSame(injector.getInstance(TI.class), injector.getInstance(TI.class));
    }

    @Test
    public void testDefaultSingletonScope_SingletonScopeMethod() {
        Injector injector = DIBootstrap
                .injectorBuilder(new SingletonModule())
                .defaultSingletonScope()
                .build();
        assertSame(injector.getInstance(TI.class), injector.getInstance(TI.class));
    }

    @Test
    public void testImplicitNoScope() {
        Injector injector = DIBootstrap
                .injectorBuilder(binder -> binder.bind(TI.class).to(TC.class))
                .build();
        assertNotSame(injector.getInstance(TI.class), injector.getInstance(TI.class));
    }

    @Test
    public void testImplicitNoScope_SingletonAnnotation() {
        Injector injector = DIBootstrap
                .injectorBuilder(binder -> binder.bind(TI.class).to(TCSingleton.class))
                .build();
        assertSame(injector.getInstance(TI.class), injector.getInstance(TI.class));
    }

    @Test
    public void testImplicitNoScope_SingletonAnnotationKey() {
        Injector injector = DIBootstrap
                .injectorBuilder(binder -> binder.bind(TI.class).to(Key.get(TCSingleton.class)))
                .build();
        assertSame(injector.getInstance(TI.class), injector.getInstance(TI.class));
    }

    @Test
    public void testImplicitNoScope_NoScopeMethod() {
        Injector injector = DIBootstrap
                .injectorBuilder(new DefaultModule())
                .build();
        assertNotSame(injector.getInstance(TI.class), injector.getInstance(TI.class));
    }

    @Test
    public void testImplicitNoScope_SingletonMethod() {
        Injector injector = DIBootstrap
                .injectorBuilder(new SingletonModule())
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

    public static class DefaultModule extends BaseBQModule {
        @Provides
        TI create() {
            return new TC();
        }
    }

    public static class SingletonModule extends BaseBQModule {
        @Provides
        @Singleton
        TI create() {
            return new TC();
        }
    }
}
