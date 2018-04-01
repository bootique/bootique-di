
package io.bootique.di.mock;

import io.bootique.di.Inject;

public class MockImplementation4Alt2 implements MockInterface4 {

    private MockInterface1 service1;
    private MockInterface3 service3;

    public MockImplementation4Alt2(@Inject("two") MockInterface1 service1,
            @Inject MockInterface3 service3) {
        this.service1 = service1;
        this.service3 = service3;
    }

    public String getName() {
        return "constructor_" + service1.getName() + "_" + service3.getName();
    }

}
