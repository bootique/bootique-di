
package io.bootique.di.mock;

import io.bootique.di.Inject;

import javax.inject.Provider;

public class MockImplementation2_ConstructorProvider implements MockInterface2 {

    private Provider<MockInterface1> provider;

    @Inject
    public MockImplementation2_ConstructorProvider(Provider<MockInterface1> provider) {
        this.provider = provider;
    }

    public String getAlteredName() {
        return "altered_" + provider.get().getName();
    }

    public String getName() {
        return "MockImplementation2Name";
    }

}
