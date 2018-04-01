package io.bootique.di.compiler.test;

import io.bootique.di.Binder;
import io.bootique.di.Module;
import io.bootique.di.Provides;

public class Module1 implements Module {

    @Provides
    public static Service1 provideService1() {
        return new Service1Impl1();
    }

    @Provides
    public static Service2 provideService2(Service1 service1) {
        return new Service2Impl1(service1);
    }

    @Override
    public void configure(Binder binder) {
    }
}
