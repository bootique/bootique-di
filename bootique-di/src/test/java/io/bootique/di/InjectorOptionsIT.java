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

import javax.inject.Inject;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class InjectorOptionsIT {

    @Test(expected = DIRuntimeException.class)
    public void testDynamicBindingDisabled() {
        Injector injector = DIBootstrap.injectorBuilder(b -> b.bind(Service.class).to(Service_Impl1.class))
                .build();

        // no binding of consumer, should throw
        injector.getInstance(Consumer1.class);
    }

    @Test
    public void testDynamicBindingEnabled() {
        Injector injector = DIBootstrap.injectorBuilder(b -> b.bind(Service.class).to(Service_Impl1.class))
                .enableDynamicBindings()
                .build();

        // no binding of consumer, but dynamic is allowed
        Consumer1 consumer = injector.getInstance(Consumer1.class);
        assertThat(consumer.service, instanceOf(Service_Impl1.class));
    }

    @Test
    public void testSingletonScope() {
        Injector injector = DIBootstrap.injectorBuilder(b -> b.bind(Service.class).to(Service_Impl1.class))
                .build();

        // no binding of consumer, but dynamic is allowed
        Service service1 = injector.getInstance(Service.class);
        assertThat(service1, instanceOf(Service_Impl1.class));

        Service service2 = injector.getInstance(Service.class);
        assertThat(service2, instanceOf(Service_Impl1.class));
        assertSame(service1, service2);
    }

    @Test
    public void testNoScopeScope() {
        Injector injector = DIBootstrap.injectorBuilder(b -> b.bind(Service.class).to(Service_Impl1.class))
                .defaultNoScope()
                .build();

        // no binding of consumer, but dynamic is allowed
        Service service1 = injector.getInstance(Service.class);
        assertThat(service1, instanceOf(Service_Impl1.class));

        Service service2 = injector.getInstance(Service.class);
        assertThat(service2, instanceOf(Service_Impl1.class));
        assertNotSame(service1, service2);
    }

    interface Service {
        String doIt();
    }

    static class Service_Impl1 implements Service {
        @Override
        public String doIt() {
            return "impl1";
        }
    }

    static class Consumer1 {
        @Inject
        Service service;
    }
}
