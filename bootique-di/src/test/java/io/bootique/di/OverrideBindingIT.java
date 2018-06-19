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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class OverrideBindingIT {

    @Test(expected = DIRuntimeException.class)
    public void testRebind_OverridesDisabled() {
        Injector injector = DIBootstrap.injectorBuilder(
                binder -> binder.bind(Foo.class).to(FooImpl1.class),
                binder -> binder.bind(Foo.class).to(FooImpl2.class)
        ).declaredOverridesOnly().build();

        injector.getInstance(Foo.class);
    }

    @Test
    public void testRebind_OverridesEnabled() {
        Injector injector = DIBootstrap.injectorBuilder(
                binder -> binder.bind(Foo.class).to(FooImpl1.class),
                binder -> binder.bind(Foo.class).to(FooImpl2.class)
        ).build();

        Foo foo = injector.getInstance(Foo.class);
        assertThat(foo, instanceOf(FooImpl2.class));
    }

    @Test
    public void testOverride_OverridesDisabled() {
        Injector injector = DIBootstrap.injectorBuilder(
                binder -> binder.bind(Foo.class).to(FooImpl1.class),
                binder -> binder.override(Foo.class).to(FooImpl2.class)
        ).declaredOverridesOnly().build();

        Foo foo = injector.getInstance(Foo.class);
        assertThat(foo, instanceOf(FooImpl2.class));
    }

    @Test
    public void testDoubleOverride_OverridesDisabled() {
        Injector injector = DIBootstrap.injectorBuilder(
                binder -> binder.bind(Foo.class).to(FooImpl1.class),
                binder -> binder.override(Foo.class).to(FooImpl2.class),
                binder -> binder.override(Foo.class).to(FooImpl3.class)
        ).declaredOverridesOnly().build();

        Foo foo = injector.getInstance(Foo.class);
        assertThat(foo, instanceOf(FooImpl3.class));
    }

    @Test
    public void testOverride_OverridesEnabled() {
        Injector injector = DIBootstrap.injectorBuilder(
                binder -> binder.bind(Foo.class).to(FooImpl1.class),
                binder -> binder.override(Foo.class).to(FooImpl2.class)
        ).declaredOverridesOnly().build();

        Foo foo = injector.getInstance(Foo.class);
        assertThat(foo, instanceOf(FooImpl2.class));
    }

    @Test(expected = DIRuntimeException.class)
    public void testOverride_NoBinding() {
        Injector injector = DIBootstrap
                .injectorBuilder(binder -> binder.override(Foo.class).to(FooImpl2.class))
                .declaredOverridesOnly().build();

        injector.getInstance(Foo.class);
    }

    interface Foo {
    }

    private static class FooImpl1 implements Foo {
    }

    private static class FooImpl2 implements Foo {
    }

    private static class FooImpl3 implements Foo {
    }

}
