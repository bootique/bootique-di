
package io.bootique.di.mock;

import io.bootique.di.Inject;

import javax.inject.Provider;

public class MockInterface1_Decorator4 implements MockInterface1 {

    private Provider<MockInterface1> delegate;

    @Inject
    public MockInterface1_Decorator4(Provider<MockInterface1> delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return "[4" + delegate.get().getName() + "4]";
    }
}