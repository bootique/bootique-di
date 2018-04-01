
package io.bootique.di.mock;


import javax.inject.Provider;

public class MockInterface1Provider implements Provider<MockInterface1> {

    public MockInterface1 get() {
        return new MockImplementation1();
    }
}
