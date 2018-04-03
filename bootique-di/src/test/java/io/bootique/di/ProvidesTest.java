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

    public static class TestModule_StandaloneService extends BaseModule {

        @Provides
        public static Service1 provideService1() {
            return () -> "provideService1";
        }
    }

    public static class TestModule_ServiceChain extends BaseModule {

        @Provides
        public static Service1 provideService1() {
            return () -> "provideService1";
        }

        @Provides
        public static Service2 provideService2(Service1 s1) {
            return () -> "provideService2_" + s1.doIt();
        }
    }

    public static class TestModule_InvalidProvider extends BaseModule {

        @Provides
        public void invalidProvides() {
        }
    }

    public static class TestModule_NamedService extends BaseModule {

        @Named("s1")
        @Provides
        public static Service1 provideService1() {
            return () -> "provideService1";
        }
    }

    public static class TestModule_NamedParameter extends BaseModule {

        @Named("s1")
        @Provides
        public static Service1 provideService1() {
            return () -> "provideService1";
        }

        @Provides
        public static Service2 provideService2(@Named("s1") Service1 s1) {
            return () -> "provideService2_" + s1.doIt();
        }
    }
}



