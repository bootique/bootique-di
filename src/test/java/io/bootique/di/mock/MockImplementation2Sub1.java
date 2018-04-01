
package io.bootique.di.mock;

import io.bootique.di.Inject;

public class MockImplementation2Sub1 extends MockImplementation2 {

    @Inject
    private MockInterface3 mockInterface3;

    @Override
    public String getAlteredName() {
        return super.getAlteredName() + ":" + mockInterface3.getName();
    }
}
