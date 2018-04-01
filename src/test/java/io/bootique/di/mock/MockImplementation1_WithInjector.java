
package io.bootique.di.mock;

import io.bootique.di.Inject;
import io.bootique.di.Injector;


public class MockImplementation1_WithInjector implements MockInterface1 {

    @Inject
    private Injector injector;
    
    public String getName() {
        return injector != null ? "injector_not_null" : "injector_null";
    }

}
