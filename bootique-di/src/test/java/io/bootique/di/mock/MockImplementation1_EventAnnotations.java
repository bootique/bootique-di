
package io.bootique.di.mock;

import io.bootique.di.BeforeScopeEnd;

public class MockImplementation1_EventAnnotations extends
        MockImplementation1_EventAnnotationsBase implements MockInterface1 {

    public String getName() {
        return "XuI";
    }

    @BeforeScopeEnd
    public void onShutdown1() {
        shutdown1 = true;
    }

    @BeforeScopeEnd
    public void onShutdown2() {
        shutdown2 = true;
    }
}
