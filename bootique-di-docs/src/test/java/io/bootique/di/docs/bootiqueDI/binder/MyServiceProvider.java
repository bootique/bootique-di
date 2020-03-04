package io.bootique.di.docs.bootiqueDI.binder;

import io.bootique.di.DIBootstrap;
import io.bootique.di.Key;

import javax.inject.Inject;
import javax.inject.Provider;

// tag::ServiceProvider[]
class MyServiceProvider implements Provider<Service> {
    // end::ServiceProvider[]
    public MyServiceProvider() {
    }
    // tag::ServiceProvider[]

    private SomeOtherService otherService;

    @Inject
    public MyServiceProvider(SomeOtherService otherService) {
        this.otherService = otherService;
    }

    @Override
    public Service get() {
        return new MyService(otherService.getSomething());
    }
    // end::ServiceProvider[]

    private class SomeOtherService {
        public Object getSomething() {
            return null;
        }
    }

    private class MyService implements Service {
        public MyService(Object something) {
        }

        public MyService() {
            DIBootstrap.InjectorBuilder injectorBuilder = DIBootstrap.injectorBuilder(

                    binder ->
                            // tag::Bind1[]
                            binder.bind(Service.class).to(MyService.class)
                    // end::Bind1[]
                    , binder ->
                            // tag::Bind2[]
                            binder.bind(Service.class).toInstance(new MyService())
                    // end::Bind2[]
                    , binder ->
                            // tag::Bind3[]
                            binder.bind(Service.class).toProvider(MyServiceProvider.class)
                    // end::Bind3[]
                    , binder ->
                            // tag::Bind4[]
                            binder.bind(Service.class).toProvider(MyServiceProvider.class)
                    // end::Bind4[]
                    , binder ->
                            // tag::Bind5[]
                            binder.bind(MyServiceProvider.class).to(MyServiceProviderImpl.class)
                    // end::Bind5[]
                    , binder ->
                            // tag::Bind6[]
                            binder.bind(Service.class).toProviderInstance(new MyServiceProvider())
                    // end::Bind6[]
                    , binder ->
                            // tag::Bind7[]
                            binder.bind(Service.class, "internal").to(MyInternalService.class)
                    // end::Bind7[]
                    , binder ->
                            // tag::Bind8[]
                            binder.bind(Service.class, "public").to(MyPublicService.class)
                    // end::Bind8[]
                    , binder ->
                            // tag::Bind9[]
                            //...
                            binder.bind(String.class, Marker.class).toInstance("My string")
                    // end::Bind9[]
            );
        }
    }

    private static class MyServiceProviderImpl extends MyServiceProvider {
        public MyServiceProviderImpl(SomeOtherService otherService) {
            super(otherService);
        }
    }

    private static class MyInternalService implements Service {
    }

    private static class MyPublicService implements Service {
    }
    // tag::ServiceProvider[]
}
// end::ServiceProvider[]

