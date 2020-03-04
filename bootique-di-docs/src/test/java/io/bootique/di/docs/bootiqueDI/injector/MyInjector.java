package io.bootique.di.docs.bootiqueDI.injector;

import io.bootique.di.Injector;
import io.bootique.di.Key;
import io.bootique.di.TypeLiteral;

import javax.inject.Provider;

public class MyInjector {

    Injector injector = null;

    // tag::Injector[]

    // get directly by the class
    Worker worker = injector.getInstance(Worker.class);

    // get by the key
    Key<Service<Integer>> serviceKey = Key.get(new TypeLiteral<Service<Integer>>(){});
    Provider<Service<Integer>> serviceProvider = injector.getProvider(serviceKey);

    // end::Injector[]

    private class Worker {
    }

    private class Service<T> {
    }
}
