
package io.bootique.di.mock;

import io.bootique.di.Inject;

public class MockInterface1_Decorator1 implements MockInterface1 {

    @Inject
    private MockInterface1 delegate;

    @Override
    public String getName() {
        return "[" + delegate.getName() + "]";
    }
}
