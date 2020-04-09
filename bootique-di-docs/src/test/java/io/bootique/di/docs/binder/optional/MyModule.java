package io.bootique.di.docs.binder.optional;

import io.bootique.di.BQModule;
import io.bootique.di.Binder;
import io.bootique.di.Provides;

import java.util.Optional;

// tag::MyModule[]
class MyModule implements BQModule {

    @Override
    public void configure(Binder binder) {
        binder.bindOptional(Service.class);
    }

    @Provides
    Worker createWorker(Optional<Service> service) {
        return new MyWorker(service.orElse(new DefaultService()));
    }
    // end::MyModule[]

    private class Service {
    }

    private class Worker {
    }

    private class MyWorker extends Worker {
        public MyWorker(Service service) {
            super();
        }
    }

    private class DefaultService extends Service {
    }
    // tag::MyModule[]
}
// end::MyModule[]