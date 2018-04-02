package io.bootique.di;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProvidesTest {

    @Test
    public void testProvides_Standalone() {
        Injector injector = DIBootstrap.createInjector(new TestModule1());

        Service1 s1 = injector.getInstance(Service1.class);
        assertEquals("provideService1", s1.doIt());
    }

    @Test
    public void testProvides_Chain() {
        Injector injector = DIBootstrap.createInjector(new TestModule2());

        Service2 s2 = injector.getInstance(Service2.class);
        assertEquals("provideService2_provideService1", s2.doIt());
    }

    interface Service1 {
        String doIt();
    }

    interface Service2 {
        String doIt();
    }

    static class TestModule1 implements Module {

        @Provides
        public static Service1 provideService1() {
            return () -> "provideService1";
        }

        @Override
        public void configure(Binder binder) {
        }
    }

    static class TestModule2 implements Module {

        @Provides
        public static Service1 provideService1() {
            return () -> "provideService1";
        }

        @Provides
        public static Service1 provideService2(Service1 s1) {
            return () -> "provideService2_" + s1.doIt();
        }

        @Override
        public void configure(Binder binder) {
        }
    }
}



