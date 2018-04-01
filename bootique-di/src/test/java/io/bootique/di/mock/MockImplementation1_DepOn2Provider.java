package io.bootique.di.mock;

import javax.inject.Inject;
import javax.inject.Provider;

public class MockImplementation1_DepOn2Provider implements MockInterface1 {

    // this creates a circular dependency when MockImplementation2 is bound to
    // MockInterface2, however injecting a provider should prevent errors...
    @Inject
    private Provider<MockInterface2> interface2Provider;

    public String getName() {
        return interface2Provider.get().getName();
    }

}
