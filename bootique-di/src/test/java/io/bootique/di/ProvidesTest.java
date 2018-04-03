package io.bootique.di;

import org.junit.Test;

import javax.inject.Named;

import static org.junit.Assert.assertEquals;

public class ProvidesTest {

    @Test
    public void testProvides_Standalone() {
        Injector injector = DIBootstrap.createInjector(new TestModule_StandaloneService());

        Service1 s1 = injector.getInstance(Service1.class);
        assertEquals("provideService1", s1.doIt());
    }

    @Test
    public void testProvides_Standalone_Named() {
        Injector injector = DIBootstrap.createInjector(new TestModule_NamedService());

        Service1 s1 = injector.getInstance(Key.get(Service1.class, "s1"));
        assertEquals("provideService1", s1.doIt());
    }

    @Test
    public void testProvides_Chain() {
        Injector injector = DIBootstrap.createInjector(new TestModule_ServiceChain());

        Service2 s2 = injector.getInstance(Service2.class);
        assertEquals("provideService2_provideService1", s2.doIt());
    }

    @Test
    public void testProvides_Chain_NamedPartameter() {
        Injector injector = DIBootstrap.createInjector(new TestModule_NamedParameter());

        Service2 s2 = injector.getInstance(Service2.class);
        assertEquals("provideService2_provideService1", s2.doIt());
    }

    @Test(expected = DIRuntimeException.class)
    public void testProvides_Invalid() {
        DIBootstrap.createInjector(new TestModule_InvalidProvider());
    }

    interface Service1 {
        String doIt();
    }

    interface Service2 {
        String doIt();
    }

    public static class TestModule_StandaloneService implements Module {

        @Provides
        public static Service1 provideService1() {
            return () -> "provideService1";
        }

        @Override
        public void configure(Binder binder) {
        }
    }

    public static class TestModule_ServiceChain implements Module {

        @Provides
        public static Service1 provideService1() {
            return () -> "provideService1";
        }

        @Provides
        public static Service2 provideService2(Service1 s1) {
            return () -> "provideService2_" + s1.doIt();
        }

        @Override
        public void configure(Binder binder) {
        }
    }

    public static class TestModule_InvalidProvider implements Module {

        @Provides
        public void invalidProvides() {
        }

        @Override
        public void configure(Binder binder) {
        }
    }

    public static class TestModule_NamedService implements Module {

        @Named("s1")
        @Provides
        public static Service1 provideService1() {
            return () -> "provideService1";
        }

        @Override
        public void configure(Binder binder) {
        }
    }

    public static class TestModule_NamedParameter implements Module {

        @Named("s1")
        @Provides
        public static Service1 provideService1() {
            return () -> "provideService1";
        }

        @Provides
        public static Service2 provideService2(@Named("s1") Service1 s1) {
            return () -> "provideService2_" + s1.doIt();
        }

        @Override
        public void configure(Binder binder) {
        }
    }
}



