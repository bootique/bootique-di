package io.bootique.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ChainedBindingsIT {

    @Test
    public void testChainedBindingDirect() {
        Injector injector = DIBootstrap.injectorBuilder(binder -> {
            binder.bind(Service.class).to(SubService.class);
            binder.bind(SubService.class).to(ServiceImpl.class);
        }).build();

        Service service = injector.getInstance(Service.class);
        assertNotNull(service);
        assertEquals("ServiceImpl", service.doSomething());
    }

    @Test
    public void testChainedBindingProvideMethod() {
        Injector injector = DIBootstrap
                .injectorBuilder(new MainModule(), new SubModule())
                .build();

        Service service = injector.getInstance(Key.get(Service.class, TestQualifier.class));
        assertNotNull(service);
        assertEquals("ServiceImpl", service.doSomething());
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface TestQualifier {
    }

    static class MainModule implements BQModule {
        @Override
        public void configure(Binder binder) {
            binder.bind(Service.class, TestQualifier.class)
                    .to(SubService.class)
                    .inSingletonScope();
        }
    }

    static class SubModule extends BaseBQModule {
        @Provides
        public SubService createService() {
            return new ServiceImpl();
        }
    }

    interface Service {
        String doSomething();
    }

    interface SubService extends Service {
    }

    static class ServiceImpl implements SubService {
        @Override
        public String doSomething() {
            return "ServiceImpl";
        }
    }
}
