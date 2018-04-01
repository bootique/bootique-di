
package io.bootique.di.spi;

import io.bootique.di.DIRuntimeException;
import io.bootique.di.Module;
import io.bootique.di.mock.MockImplementation1_DepOn2;
import io.bootique.di.mock.MockImplementation1_DepOn2Constructor;
import io.bootique.di.mock.MockImplementation1_DepOn2Provider;
import io.bootique.di.mock.MockImplementation2;
import io.bootique.di.mock.MockImplementation2_Constructor;
import io.bootique.di.mock.MockImplementation2_I3Dependency;
import io.bootique.di.mock.MockImplementation3;
import io.bootique.di.mock.MockInterface1;
import io.bootique.di.mock.MockInterface2;
import io.bootique.di.mock.MockInterface3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DefaultInjectorCircularInjectionTest {

    @Test
    public void testFieldInjection_CircularDependency() {

        Module module = binder -> {
            binder.bind(MockInterface1.class).to(MockImplementation1_DepOn2.class);
            binder.bind(MockInterface2.class).to(MockImplementation2.class);
        };

        DefaultInjector injector = new DefaultInjector(module);

        try {
            injector.getInstance(MockInterface1.class);
            fail("Circular dependency is not detected.");
        }
        catch (DIRuntimeException e) {
            // expected
        }
        catch (StackOverflowError e) {
            fail("Circular dependency is not detected, causing stack overflow");
        }
    }

    @Test
    public void testProviderInjection_CircularDependency() {

        Module module = binder -> {
            binder.bind(MockInterface1.class).to(
                    MockImplementation1_DepOn2Provider.class);
            binder.bind(MockInterface2.class).to(MockImplementation2.class);
        };

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface1 service = injector.getInstance(MockInterface1.class);
        assertEquals("MockImplementation2Name", service.getName());
    }

    @Test
    public void testConstructorInjection_CircularDependency() {

        Module module = binder -> {
            binder.bind(MockInterface1.class).to(
                    MockImplementation1_DepOn2Constructor.class);
            binder.bind(MockInterface2.class).to(
                    MockImplementation2_Constructor.class);
        };

        DefaultInjector injector = new DefaultInjector(module);

        try {
            injector.getInstance(MockInterface1.class);
            fail("Circular dependency is not detected.");
        }
        catch (DIRuntimeException e) {
            // expected
        }
        catch (StackOverflowError e) {
            fail("Circular dependency is not detected, causing stack overflow");
        }
    }

    @Test
    public void testConstructorInjection_WithFieldInjectionDeps() {

        Module module = binder -> {
            binder.bind(MockInterface1.class).to(
                    MockImplementation1_DepOn2Constructor.class);
            binder.bind(MockInterface2.class).to(
                    MockImplementation2_I3Dependency.class);
            binder.bind(MockInterface3.class).to(MockImplementation3.class);
        };

        DefaultInjector injector = new DefaultInjector(module);

        try {
            injector.getInstance(MockInterface1.class);
        }
        catch (DIRuntimeException e) {
            fail("Circular dependency is detected incorrectly: " + e.getMessage());
        }
    }
}
