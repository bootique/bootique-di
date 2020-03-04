package io.bootique.di.docs.bootiqueDI.core;

// tag::Application[]
import javax.inject.Inject;

// end::Application[]
import javax.inject.Provider;
// tag::Application[]
import io.bootique.di.DIBootstrap;
import io.bootique.di.Injector;

public class Application {
    public static void main(String[] args) {
        Injector injector = DIBootstrap
                .injectorBuilder(
                        binder -> binder.bind(Service.class)
                                .to(MyService.class) // <!--1-->
                                .inSingletonScope(),
                        binder -> binder.bind(Worker.class).to(MyWorker.class)
                )
                .build();
        Worker worker = injector.getInstance(Worker.class);
        worker.doWork();
    }
}

interface Worker {
    void doWork();
}

interface Service {
    String getInfo();
}

// tag::Provider[]
class MyService implements Service {
    // end::Provider[]

    @Override
    public String getInfo() {
        return "Hello world!";
    }
    // end::Application[]
    // tag::Provider[]

    private Provider<Worker> workerProvider;

    @Inject
    MyService(Provider<Worker> workerProvider) {
        this.workerProvider = workerProvider;
    }

    // ...
    // tag::Application[]
}

class MyWorker implements Worker {
    private Service service;

    @Inject
    MyWorker(Service service) {
        this.service = service;
    }
    // end::Application[]

    // ...
    // end::Provider[]

    // tag::Application[]

    public void doWork() {
        System.out.println(service.getInfo());
    }
    // tag::Provider[]
}
// end::Provider[]
// end::Application[]
