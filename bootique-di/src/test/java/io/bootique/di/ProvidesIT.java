package io.bootique.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Test;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Qualifier;

import static org.junit.Assert.assertEquals;

public class ProvidesIT {

    @Test
    public void testProvides_Standalone_Static() {
        Injector injector = DIBootstrap.createInjector(new TestModule_StandaloneService_Static());

        Service1 s1 = injector.getInstance(Service1.class);
        assertEquals("provideService1", s1.doIt());
    }

    @Test
    public void testProvides_Standalone_Instance() {
        Injector injector = DIBootstrap.createInjector(new TestModule_StandaloneService_Instance());

        Service1 s1 = injector.getInstance(Service1.class);
        assertEquals("provideService1", s1.doIt());
    }

    @Test
    public void testProvides_Standalone_AnonymousClass() {
        Injector injector = DIBootstrap.createInjector(new BaseModule() {
            @Provides
            Service1 createService() {
                return () -> "provideService1";
            }
        });

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
    public void testProvides_Chain_NamedParameter() {
        Injector injector = DIBootstrap.createInjector(new TestModule_NamedParameter());

        Service2 s2 = injector.getInstance(Service2.class);
        assertEquals("provideService2_provideService1", s2.doIt());
    }

    @Test
    public void testProvides_Chain_ProviderParameter() {
        Injector injector = DIBootstrap.createInjector(new TestModule_ServiceChain_ProviderParameter());

        Service2 s2 = injector.getInstance(Service2.class);
        assertEquals("provideService2_provideService1", s2.doIt());
    }

    @Test
    public void testProvides_Chain_QualifiedProviderParameter() {
        Injector injector = DIBootstrap.createInjector(new TestModule_QualifiedProviderParameter());

        Service2 s2 = injector.getInstance(Service2.class);
        assertEquals("provideService2_provideService1", s2.doIt());
    }

    @Test(expected = DIRuntimeException.class)
    public void testProvides_Invalid() {
        DIBootstrap.createInjector(new TestModule_InvalidProvider());
    }

    @Test(expected = DIRuntimeException.class)
    public void testProvides_InvalidQualifier() {
        DIBootstrap.createInjector(new TestModule_InvalidQualifier());
    }

    interface Service1 {
        String doIt();
    }

    interface Service2 {
        String doIt();
    }

    public static class TestModule_StandaloneService_Static extends BaseModule {

        @Provides
        public static Service1 provideService1() {
            return () -> "provideService1";
        }
    }

    public static class TestModule_StandaloneService_Instance extends BaseModule {

        @Provides
        public Service1 provideService1() {
            return () -> "provideService1";
        }
    }

    public static class TestModule_ServiceChain extends BaseModule {

        @Provides
        public static Service1 provideService1() {
            return () -> "provideService1";
        }

        @Provides
        static Service2 provideService2(Service1 s1) {
            return () -> "provideService2_" + s1.doIt();
        }
    }

    public static class TestModule_ServiceChain_ProviderParameter extends BaseModule {

        @Provides
        public static Service1 provideService1() {
            return () -> "provideService1";
        }

        @Provides
        static Service2 provideService2(Provider<Service1> s1) {
            return () -> "provideService2_" + s1.get().doIt();
        }
    }

    public static class TestModule_InvalidProvider extends BaseModule {

        @Provides
        public void invalidProvides() {
        }
    }

    public static class TestModule_InvalidQualifier extends BaseModule {

        @TestQualifier
        @Named("s1")
        @Provides
        public Service1 invalidProvides() {
            return () -> "provideService1";
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

        @Provides
        public static Service1 provideUnnamedService1() {
            return () -> "provideService1_unnamed";
        }

        @Named("s1")
        @Provides
        public static Service1 provideService1() {
            return () -> "provideService1";
        }

        @Provides
        public Service2 provideService2(@Named("s1") Service1 s1) {
            return () -> "provideService2_" + s1.doIt();
        }
    }

    public static class TestModule_QualifiedProviderParameter extends BaseModule {

        @Provides
        public static Service1 provideUnnamedService1() {
            return () -> "provideService1_unqualified";
        }

        @TestQualifier
        @Provides
        public static Service1 provideService1() {
            return () -> "provideService1";
        }

        @Provides
        public Service2 provideService2(@TestQualifier Provider<Service1> s1) {
            return () -> "provideService2_" + s1.get().doIt();
        }
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface TestQualifier {
    }
}



