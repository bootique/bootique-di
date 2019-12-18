package io.bootique.di;

import javax.inject.Provider;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class BQInjectIT {

    @Test
    public void testConstructorInjection() {
        Injector injector = DIBootstrap.injectorBuilder(
                b -> {
                    b.bind(Service.class).to(Service_Impl1.class);
                    b.bind(Consumer1.class);
                })
                .build();

        Consumer1 consumer = injector.getInstance(Consumer1.class);
        assertThat(consumer.service, instanceOf(Service_Impl1.class));
    }

    @Test
    public void testFieldInjection() {
        Injector injector = DIBootstrap.injectorBuilder(
                b -> {
                    b.bind(Service.class).to(Service_Impl1.class);
                    b.bind(Consumer2.class);
                })
                .build();

        Consumer2 consumer = injector.getInstance(Consumer2.class);
        assertThat(consumer.service, instanceOf(Service_Impl1.class));
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

        @BQInject
        Consumer1(Provider<Service> serviceProvider) {
            service = serviceProvider.get();
        }
    }

    static class Consumer2 {
        @BQInject
        Service service;
    }

}
