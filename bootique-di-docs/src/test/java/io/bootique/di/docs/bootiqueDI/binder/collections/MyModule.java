package io.bootique.di.docs.bootiqueDI.binder.collections;

import io.bootique.di.BQModule;
import io.bootique.di.Binder;
import io.bootique.di.Key;
import io.bootique.di.Provides;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Set;

// tag::MyModule[]
class MyModule implements BQModule {

    @Override
    public void configure(Binder binder) {
        binder.bindSet(Service.class)
                .add(DefaultService.class)
                .add(Key.get(Service.class, "internal"))
                .addProvider(ServiceProvider.class);
    }

    @Provides
    @Singleton
    @Named("internal")
    Service createInternalService() {
        return new InternalService();
    }

    @Provides
    @Singleton
    Worker createWorker(Set<Service> services) {
        return new MyWorker(services);
    }
    // end::MyModule[]

    private static class Service {
    }

    private static class DefaultService extends Service {
    }

    private static class ServiceProvider implements Provider<DefaultService> {
        @Override
        public DefaultService get() {
            return null;
        }
    }

    private static class InternalService extends Service {
    }

    private static class Worker {
    }

    private static class MyWorker extends Worker {
        public MyWorker(Set<Service> services) {
            super();
        }
    }
    // tag::MyModule[]
}
// end::MyModule[]
