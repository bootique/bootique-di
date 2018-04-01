
package io.bootique.di.mock;

import javax.inject.Inject;

public class MockInterface1_Decorator2 implements MockInterface1 {

    private MockInterface1 delegate;

    @Inject
    public MockInterface1_Decorator2(MockInterface1 delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return "{" + delegate.getName() + "}";
    }
}