
package io.bootique.di.spi;

import io.bootique.di.Module;
import io.bootique.di.mock.MockImplementation1_EventAnnotations;
import io.bootique.di.mock.MockInterface1;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultInjectorTest {

    @Test
    public void testConstructor_Empty() {
        new DefaultInjector();
        // no exceptions...
    }

    @Test
    public void testConstructor_SingleModule() {
        final boolean[] configureCalled = new boolean[1];

        Module module = binder -> configureCalled[0] = true;

        new DefaultInjector(module);
        assertTrue(configureCalled[0]);
    }

    @Test
    public void testConstructor_MultiModule() {

        final boolean[] configureCalled = new boolean[2];

        Module module1 = binder -> configureCalled[0] = true;

        Module module2 = binder -> configureCalled[1] = true;

        new DefaultInjector(module1, module2);
        assertTrue(configureCalled[0]);
        assertTrue(configureCalled[1]);
    }

    @Test
    public void testShutdown() {

        MockImplementation1_EventAnnotations.reset();

        Module module = binder -> binder
                .bind(MockInterface1.class)
                .to(MockImplementation1_EventAnnotations.class)
                .inSingletonScope();

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface1 instance1 = injector.getInstance(MockInterface1.class);
        assertEquals("XuI", instance1.getName());

        assertFalse(MockImplementation1_EventAnnotations.shutdown1);
        assertFalse(MockImplementation1_EventAnnotations.shutdown2);
        assertFalse(MockImplementation1_EventAnnotations.shutdown3);

        injector.shutdown();

        assertTrue(MockImplementation1_EventAnnotations.shutdown1);
        assertTrue(MockImplementation1_EventAnnotations.shutdown2);
        assertTrue(MockImplementation1_EventAnnotations.shutdown3);
    }

}
