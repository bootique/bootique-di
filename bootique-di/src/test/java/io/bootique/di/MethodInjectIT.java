package io.bootique.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Qualifier;

import org.junit.Test;

import static org.junit.Assert.*;

public class MethodInjectIT {

    @Test
    public void testMethodInjectDisabledByDefault() {
        Injector injector = DIBootstrap.injectorBuilder(binder -> {
            binder.bind(Service.class).to(Service_Impl1.class);
            binder.bind(Consumer_Impl.class).to(Consumer_Impl.class);
        }).build();

        Consumer_Impl consumer = injector.getInstance(Consumer_Impl.class);
        assertFalse(consumer.serviceSet);
    }

    @Test
    public void testMethodInject() {
        Injector injector = DIBootstrap.injectorBuilder(binder -> {
            binder.bind(Service.class).to(Service_Impl1.class);
            binder.bind(Consumer_Impl.class).to(Consumer_Impl.class);
        }).enableMethodInjection().build();

        Consumer_Impl consumer = injector.getInstance(Consumer_Impl.class);
        assertTrue(consumer.serviceSet);
    }

    @Test
    public void testInjectMembers() {
        Injector injector = DIBootstrap.injectorBuilder(binder -> {
            binder.bind(Service.class).to(Service_Impl1.class);
            binder.bind(Consumer_Impl.class).to(Consumer_Impl.class);
        }).enableMethodInjection().build();

        Consumer_Impl consumer = new Consumer_Impl();
        injector.injectMembers(consumer);
        assertTrue("Method not injected", consumer.serviceSet);
    }

    @Test
    public void testInjectMethod_QualifiedParameter() {
        Injector injector = DIBootstrap.injectorBuilder(binder -> {
            binder.bind(Key.get(Service.class, CustomQualifier.class)).to(Service_Impl1.class);
            binder.bind(Consumer_Impl2.class).to(Consumer_Impl2.class);
        }).enableMethodInjection().build();

        Consumer_Impl2 consumer = injector.getInstance(Consumer_Impl2.class);
        assertTrue(consumer.serviceSet);
    }


    interface Service {
    }

    static class Service_Impl1 implements Service {
    }

    static class Consumer_Impl {

        private boolean serviceSet;

        @Inject
        void setService(Service service) {
            Objects.requireNonNull(service);
            serviceSet = true;
        }
    }

    static class Consumer_Impl2 {

        private boolean serviceSet;

        @Inject
        void setService(@CustomQualifier Service service) {
            Objects.requireNonNull(service);
            serviceSet = true;
        }
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface CustomQualifier {
    }


}
