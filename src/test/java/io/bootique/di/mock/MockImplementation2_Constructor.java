
package io.bootique.di.mock;

import io.bootique.di.Inject;

public class MockImplementation2_Constructor implements MockInterface2 {

    private MockInterface1 service;

    public MockImplementation2_Constructor(@Inject MockInterface1 service) {
        this.service = service;
    }

    public String getAlteredName() {
        return "altered_" + service.getName();
    }

    public String getName() {
        return "MockImplementation2Name";
    }
}
