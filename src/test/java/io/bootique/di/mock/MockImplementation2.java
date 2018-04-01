
package io.bootique.di.mock;

import io.bootique.di.Inject;

public class MockImplementation2 implements MockInterface2 {

    @Inject
    private MockInterface1 service;

    public String getAlteredName() {
        return "altered_" + service.getName();
    }
    
    public String getName() {
        return "MockImplementation2Name";
    }

}
