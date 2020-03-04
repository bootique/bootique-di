package io.bootique.di.docs.bootiqueDI.module;

import io.bootique.di.BQModule;
import io.bootique.di.Binder;
import io.bootique.di.Provides;

// tag::Module[]
class MyModule implements BQModule {
    public void configure(Binder binder) {
    }

    @Provides
    Service createService() {
        return new MyService("service");
    }

    @Provides
    Worker createWorker(Service service) {
        return new MyWorker(service);
    }
    // end::Module[]

    private class MyService extends Service {
        public MyService(String service) {
            super();
        }
    }

    private class Service {
    }

    private class Worker {
    }

    private class MyWorker extends Worker {
        public MyWorker(Service service) {
            super();
        }
    }
    // tag::Module[]
}
// end::Module[]
