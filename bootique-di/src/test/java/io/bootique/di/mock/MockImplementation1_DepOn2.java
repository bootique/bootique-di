package io.bootique.di.mock;

import javax.inject.Inject;

public class MockImplementation1_DepOn2 implements MockInterface1 {

    // this creates a circular dependency when MockImplementation2 is bound to
    // MockInterface2.
    @Inject
    private MockInterface2 interface2;

    public String getName() {
        return interface2.getName();
    }

}
