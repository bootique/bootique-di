
package io.bootique.di.mock;

import io.bootique.di.DIRuntimeException;

import javax.inject.Provider;

public class MockImplementation1_Provider implements Provider<MockInterface1> {

    public MockInterface1 get() throws DIRuntimeException {
        return new MockImplementation1();
    }
}
