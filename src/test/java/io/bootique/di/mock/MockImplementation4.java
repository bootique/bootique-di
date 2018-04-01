
package io.bootique.di.mock;

import io.bootique.di.Inject;

public class MockImplementation4 implements MockInterface4 {

    private MockInterface1 service;

    public MockImplementation4() {
        throw new UnsupportedOperationException(
                "This constructor should not be picked for DI purposes");
    }

    public MockImplementation4(@Inject MockInterface1 service) {
        this.service = service;
    }

    public MockImplementation4(int i, int k) {
        throw new UnsupportedOperationException(
                "This constructor should not be picked for DI purposes");
    }

    public String getName() {
        return "constructor_" + service.getName();
    }

}
