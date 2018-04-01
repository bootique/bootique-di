
package io.bootique.di.mock;

import io.bootique.di.Inject;

public class MockImplementation2_I3Dependency implements MockInterface2 {

    @Inject
    private MockInterface3 delegate;

    public String getAlteredName() {
        return "dT" + delegate.getName();
    }

    public String getName() {
        return "dT";
    }
}
