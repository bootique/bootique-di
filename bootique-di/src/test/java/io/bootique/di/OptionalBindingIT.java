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

import java.util.Optional;
import javax.inject.Inject;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class OptionalBindingIT {

    @Test(expected = DIRuntimeException.class)
    public void testMandatoryBinding() {
        Injector injector = DIBootstrap.createInjector(b -> {
            b.bind(Consumer1.class);
        });

        // should throw, no Service bound
        injector.getInstance(Consumer1.class);
    }

    @Test
    public void testOptionalBinding() {
        Injector injector = DIBootstrap.createInjector(b -> {
            b.bindOptional(Service.class);
            b.bind(Consumer1.class);
        });

        Consumer1 consumer1 = injector.getInstance(Consumer1.class);
        assertNull(consumer1.service);
        assertEquals(Optional.empty(), consumer1.optionalService);
    }

    @Test
    public void testBoundOptionalBinding() {
        Injector injector = DIBootstrap.createInjector(b -> {
            b.bindOptional(Service.class).to(Service_Impl1.class);
            b.bind(Consumer1.class);
        });

        Consumer1 consumer1 = injector.getInstance(Consumer1.class);
        assertThat(consumer1.service, instanceOf(Service_Impl1.class));
        assertNotNull(consumer1.optionalService);
        assertTrue(consumer1.optionalService.isPresent());
        assertThat(consumer1.optionalService.get(), instanceOf(Service_Impl1.class));
    }

    @Test
    public void testOptionalBindingOverride() {
        Injector injector = DIBootstrap.createInjector(
                b -> {
                    b.bindOptional(Service.class);
                    b.bind(Consumer1.class);
                },
                b -> b.bind(Service.class).to(Service_Impl1.class)
        );

        Consumer1 consumer1 = injector.getInstance(Consumer1.class);
        assertThat(consumer1.service, instanceOf(Service_Impl1.class));
        assertNotNull(consumer1.optionalService);
        assertTrue(consumer1.optionalService.isPresent());
        assertThat(consumer1.optionalService.get(), instanceOf(Service_Impl1.class));
    }

    @Test
    public void testOptionalBindingOverrideWithOverrideDisabled() {
        Injector injector = DIBootstrap.injectorBuilder(
                b -> {
                    b.bindOptional(Service.class);
                    b.bind(Consumer1.class);
                },
                b -> b.bind(Service.class).to(Service_Impl1.class)
        ).declaredOverridesOnly().build();

        Consumer1 consumer1 = injector.getInstance(Consumer1.class);
        assertThat(consumer1.service, instanceOf(Service_Impl1.class));
        assertNotNull(consumer1.optionalService);
        assertTrue(consumer1.optionalService.isPresent());
        assertThat(consumer1.optionalService.get(), instanceOf(Service_Impl1.class));
    }

    @Test
    public void testOptionalBindingOverrideWithOptional() {
        Injector injector = DIBootstrap.createInjector(
                b -> {
                    b.bindOptional(Service.class);
                    b.bind(Consumer1.class);
                },
                b -> b.bindOptional(Service.class).to(Service_Impl1.class)
        );

        Consumer1 consumer1 = injector.getInstance(Consumer1.class);
        assertThat(consumer1.service, instanceOf(Service_Impl1.class));
        assertNotNull(consumer1.optionalService);
        assertTrue(consumer1.optionalService.isPresent());
        assertThat(consumer1.optionalService.get(), instanceOf(Service_Impl1.class));
    }

    interface Service {
    }

    private static class Service_Impl1 implements Service {
    }

    private static class Consumer1 {
        @Inject
        private Service service;

        @Inject
        private Optional<Service> optionalService;
    }

}