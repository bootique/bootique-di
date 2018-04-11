
package io.bootique.di;


import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class DIBootstrapTest {

    @Test
    public void testCreateInjector_Empty() {
        Injector emptyInjector = DIBootstrap.injectorBuilder().build();
        assertNotNull(emptyInjector);
    }

    @Test
    public void testCreateInjector_SingleModule() {
        final boolean[] configureCalled = new boolean[1];

        Module module = binder -> configureCalled[0] = true;

        Injector injector = DIBootstrap.injectorBuilder(module).build();
        assertNotNull(injector);

        assertTrue(configureCalled[0]);
    }

    @Test
    public void testCreateInjector_MultiModule() {

        final boolean[] configureCalled = new boolean[2];

        Module module1 = binder -> configureCalled[0] = true;

        Module module2 = binder -> configureCalled[1] = true;

        Injector injector = DIBootstrap.injectorBuilder(module1, module2).build();
        assertNotNull(injector);

        assertTrue(configureCalled[0]);
        assertTrue(configureCalled[1]);
    }

    @Test
    public void testCreateInjector_MultiModuleCollection() {

        final boolean[] configureCalled = new boolean[2];

        Module module1 = binder -> configureCalled[0] = true;

        Module module2 = binder -> configureCalled[1] = true;

        Injector injector = DIBootstrap.injectorBuilder(Arrays.asList(module1, module2)).build();
        assertNotNull(injector);

        assertTrue(configureCalled[0]);
        assertTrue(configureCalled[1]);
    }
}
