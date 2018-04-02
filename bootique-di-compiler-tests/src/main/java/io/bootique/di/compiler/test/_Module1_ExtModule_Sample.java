package io.bootique.di.compiler.test;

import io.bootique.di.Binder;
import io.bootique.di.Module;

import javax.annotation.Generated;
import javax.inject.Inject;
import javax.inject.Provider;

/**
 * An expected generated delegate for {@link Module1}.
 */
@Generated("io.bootique.di.compiler")
public class _Module1_ExtModule_Sample implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(Service1.class).toProvider(_Module1_ProvideService1.class);
        binder.bind(Service2.class).toProvider(_Module1_ProvideService2.class);
    }

    public static class _Module1_ProvideService1 implements Provider<Service1> {

        @Override
        public Service1 get() {
            return Module1.provideService1_Static();
        }
    }

    public class _Module1_ProvideService2 implements Provider<Service2> {

        private Provider<Service1> service1Provider;

        @Inject
        public _Module1_ProvideService2(Provider<Service1> service1Provider) {
            this.service1Provider = service1Provider;
        }

        @Override
        public Service2 get() {
            return Module1.provideService2(service1Provider.get());
        }
    }
}
