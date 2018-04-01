
package io.bootique.di.mock;

import io.bootique.di.BeforeScopeEnd;

public class MockImplementation1_EventAnnotationsBase {

    public static boolean shutdown1;
    public static boolean shutdown2;
    public static boolean shutdown3;

    public static void reset() {
        shutdown1 = false;
        shutdown2 = false;
        shutdown3 = false;
    }

    @BeforeScopeEnd
    public void onShutdown3() {
        shutdown3 = true;
    }
}
