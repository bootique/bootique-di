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
