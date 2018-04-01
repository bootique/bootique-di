package io.bootique.di.mock;

import javax.inject.Inject;
import javax.inject.Named;

public class MockImplementation4Alt implements MockInterface4 {

    private MockInterface1 service;

    @Inject
    public MockImplementation4Alt(@Named("two") MockInterface1 service) {
        this.service = service;
    }

    public String getName() {
        return "constructor_" + service.getName();
    }

}
