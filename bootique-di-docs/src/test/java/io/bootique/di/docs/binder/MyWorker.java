package io.bootique.di.docs.binder;

import javax.inject.Inject;
import javax.inject.Named;

// tag::MyWorker[]
// tag::MyWorker2[]
class MyWorker {
    // end::MyWorker2[]
    @Inject
    public MyWorker(@Named("public") Service service) {
    }
    // end::MyWorker[]
    // tag::MyWorker2[]
    @Inject
    public MyWorker(@Marker String arg) {
    }
    // tag::MyWorker[]
}
// end::MyWorker[]
// end::MyWorker2[]
