package io.bootique.di.docs.bootiqueDI.binder.generics;

import io.bootique.di.BQModule;
import io.bootique.di.Binder;
import io.bootique.di.Key;
import io.bootique.di.TypeLiteral;

public class Generics implements BQModule {

    @Override
    public void configure(Binder binder) {
        // tag::Bind[]
        binder.bind(Key.get(new TypeLiteral<Service<String>>(){})).to(MyStringService.class);
        binder.bind(Key.get(new TypeLiteral<Service<Integer>>(){})).to(MyIntegerService.class);
        // end::Bind[]
    }

    private static class MyStringService extends Service<String> {
    }

    private static class MyIntegerService extends Service<Integer> {
    }
}
