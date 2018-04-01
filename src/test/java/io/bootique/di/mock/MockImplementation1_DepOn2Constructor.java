
package io.bootique.di.mock;

import io.bootique.di.Inject;

public class MockImplementation1_DepOn2Constructor implements MockInterface1 {

    private MockInterface2 interface2;

    // this creates a circular dependency when MockImplementation2 is bound to
    // MockInterface2.
    public MockImplementation1_DepOn2Constructor(@Inject MockInterface2 interface2) {
        this.interface2 = interface2;
    }

    public String getName() {
        return interface2.getName();
    }
}
