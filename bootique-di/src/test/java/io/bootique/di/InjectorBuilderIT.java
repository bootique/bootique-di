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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class InjectorBuilderIT {

    @Test
    public void testCustomProviderWrapper() {
        boolean[] providerWrapped = {false};
        Injector injector = DIBootstrap.injectorBuilder(b -> b.bind(Service.class).to(Service_Impl1.class))
                .withProviderWrapper(p -> {
                    providerWrapped[0] = true;
                    return p;
                })
                .build();

        Service service = injector.getInstance(Service.class);
        assertThat(service, instanceOf(Service_Impl1.class));
        assertTrue(providerWrapped[0]);
    }

    @Test
    public void testCustomInjectAnnotation() {
        Injector injector = DIBootstrap.injectorBuilder(
                b -> {
                    b.bind(Service.class).to(Service_Impl1.class);
                    b.bind(Consumer2.class).to(Consumer2.class);
                })
                .withInjectAnnotationPredicate(o -> o.isAnnotationPresent(MyInject.class))
                .build();

        Consumer2 consumer = injector.getInstance(Consumer2.class);
        assertThat(consumer.service, instanceOf(Service_Impl1.class));
    }

    @Test
    public void testCustomProviderType() {
        Injector injector = DIBootstrap.injectorBuilder(
                b -> {
                    b.bind(Service.class).to(Service_Impl1.class);
                    b.bind(Consumer1.class).to(Consumer1.class);
                })
                .withProviderPredicate(MyProvider.class::equals)
                .withProviderWrapper(MyProvider::new)
                .build();

        Consumer1 consumer = injector.getInstance(Consumer1.class);
        assertThat(consumer.service, instanceOf(Service_Impl1.class));
    }

    @Test
    public void testCustomProvidesMethodPredicate() {
        BQModule module = new BaseModule() {
            // should be found by name prefix
            Service providesService() {
                return new Service_Impl1();
            }
        };

        Injector injector = DIBootstrap.injectorBuilder(module)
                .withProvidesMethodPredicate(m -> m.getName().startsWith("provides"))
                .build();

        Service service = injector.getInstance(Service.class);
        assertThat(service, instanceOf(Service_Impl1.class));
    }

    @Test
    public void testCustomQualifierPredicate() {
        Injector injector = DIBootstrap.injectorBuilder(
                b -> {
                    b.bind(Key.get(Service.class, MyQualifier.class)).to(Service_Impl1.class);
                    b.bind(Consumer3.class).to(Consumer3.class);
                })
                .withQualifierPredicate(MyQualifier.class::equals)
                .build();

        Consumer3 consumer = injector.getInstance(Consumer3.class);
        assertThat(consumer.service, instanceOf(Service_Impl1.class));
    }

    static class MyProvider<T> implements Provider<T> {
        final Provider<T> delegate;
        boolean called;

        MyProvider(Provider<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public T get() {
            called = true;
            return delegate.get();
        }
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
        Service service;

        @Inject
        Consumer1(MyProvider<Service> serviceProvider) {
            service = serviceProvider.get();
        }
    }

    static class Consumer2 {
        @MyInject
        Service service;

        @Inject
        Integer integer;
    }

    static class Consumer3 {
        @Inject
        @MyQualifier
        Service service;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface MyInject {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface MyQualifier {
    }
}
