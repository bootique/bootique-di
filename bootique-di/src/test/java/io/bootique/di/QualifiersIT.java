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

package io.bootique.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Inject;
import javax.inject.Qualifier;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class QualifiersIT {

    @Test
    public void testQualifiedInject() {
        Injector injector = DIBootstrap.createInjector(b -> {
            b.bind(Key.get(Service.class, CustomQualifier.class)).to(Service_Impl1.class);
            b.bind(Key.get(Service.class)).to(Service_Impl2.class);

            // Direct field injection
            b.bind(Consumer.class).to(Consumer_Impl1.class);
            b.bind(Key.get(Consumer.class, CustomQualifier.class)).to(Consumer_Impl2.class);
        });
        checkInjectionResult(injector);
    }

    @Test
    public void testQualifiedProvider() {
        Injector injector = DIBootstrap.createInjector(b -> {
            // Direct field injection
            b.bind(Consumer.class).to(Consumer_Impl1.class);
            b.bind(Key.get(Consumer.class, CustomQualifier.class)).to(Consumer_Impl2.class);
        }, new ServiceModule2()); // module with @Provides annotated methods, can't use anonymous class
        checkInjectionResult(injector);
    }

    @Test
    public void testQualifiedConstructorParameter() {
        Injector injector = DIBootstrap.createInjector(b -> {
            b.bind(Key.get(Service.class, CustomQualifier.class)).to(Service_Impl1.class);
            b.bind(Key.get(Service.class)).to(Service_Impl2.class);

            // Constructor injection
            b.bind(Consumer.class).to(Consumer_Impl3.class);
            b.bind(Key.get(Consumer.class, CustomQualifier.class)).to(Consumer_Impl4.class);
        });
        checkInjectionResult(injector);
    }

    @Test(expected = DIRuntimeException.class)
    public void testMultipleQualifiers() {
        Injector injector = DIBootstrap.createInjector(b -> {
            b.bind(Key.get(Service.class, CustomQualifier.class)).to(Service_Impl1.class);
            b.bind(Key.get(Service.class)).to(Service_Impl2.class);

            // Constructor injection
            b.bind(Consumer.class).to(Consumer_Impl5.class);
        });

        injector.getInstance(Consumer.class);
    }

    private void checkInjectionResult(Injector injector) {
        Consumer consumer1 = injector.getInstance(Consumer.class);
        assertThat(consumer1.getService(), instanceOf(Service_Impl1.class));

        Consumer consumer2 = injector.getInstance(Key.get(Consumer.class, CustomQualifier.class));
        assertThat(consumer2.getService(), instanceOf(Service_Impl2.class));
    }

    public static class ServiceModule2 extends BaseModule {
        @Provides
        @CustomQualifier
        public Service createService() {
            return new Service_Impl1();
        }

        @Provides
        public Service createService2() {
            return new Service_Impl2();
        }
    }

    interface Service {}
    interface Consumer {
        Service getService();
    }

    private static class Service_Impl1 implements Service {
    }

    private static class Service_Impl2 implements Service {
    }

    private static class Consumer_Impl1 implements Consumer {
        @Inject
        @CustomQualifier
        private Service service;

        @Override
        public Service getService() {
            return service;
        }
    }

    private static class Consumer_Impl2 implements Consumer {
        @Inject
        private Service service;

        @Override
        public Service getService() {
            return service;
        }
    }

    private static class Consumer_Impl3 implements Consumer {
        private Service service;

        @Inject
        public Consumer_Impl3(@CustomQualifier Service service) {
            this.service = service;
        }

        @Override
        public Service getService() {
            return service;
        }
    }

    private static class Consumer_Impl4 implements Consumer {
        private Service service;

        @Inject
        public Consumer_Impl4(Service service) {
            this.service = service;
        }

        @Override
        public Service getService() {
            return service;
        }
    }

    private static class Consumer_Impl5 implements Consumer {

        @Inject
        @CustomQualifier
        @CustomQualifier2
        private Service service;

        @Override
        public Service getService() {
            return service;
        }
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface CustomQualifier {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface CustomQualifier2 {
    }
}
