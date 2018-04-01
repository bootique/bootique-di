
package io.bootique.di.mock;

import javax.inject.Inject;
import javax.inject.Provider;

public class MockInterface1_Decorator5 implements MockInterface1 {

    @Inject
    private Provider<MockInterface1> delegate;

    @Override
    public String getName() {
        return "[5" + delegate.get().getName() + "5]";
    }
}
