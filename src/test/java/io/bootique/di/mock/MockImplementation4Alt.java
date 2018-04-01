
package io.bootique.di.mock;

import io.bootique.di.Inject;

public class MockImplementation4Alt implements MockInterface4 {

    private MockInterface1 service;

    public MockImplementation4Alt(@Inject("two") MockInterface1 service) {
        this.service = service;
    }

    public String getName() {
        return "constructor_" + service.getName();
    }

}
