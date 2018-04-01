
package io.bootique.di.mock;

import javax.inject.Inject;

public class MockImplementation2_Constructor implements MockInterface2 {

    private MockInterface1 service;

    @Inject
    public MockImplementation2_Constructor(MockInterface1 service) {
        this.service = service;
    }

    public String getAlteredName() {
        return "altered_" + service.getName();
    }

    public String getName() {
        return "MockImplementation2Name";
    }
}
