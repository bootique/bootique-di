
package io.bootique.di.mock;

import io.bootique.di.Inject;

public class MockInterface1_Decorator2 implements MockInterface1 {

    private MockInterface1 delegate;

    public MockInterface1_Decorator2(@Inject MockInterface1 delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return "{" + delegate.getName() + "}";
    }
}