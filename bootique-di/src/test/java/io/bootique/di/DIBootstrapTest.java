
package io.bootique.di;

import io.bootique.di.DIBootstrap;
import io.bootique.di.Injector;
import io.bootique.di.Module;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class DIBootstrapTest {

    @Test
	public void testCreateInjector_Empty() {
		Injector emptyInjector = DIBootstrap.createInjector();
		assertNotNull(emptyInjector);
	}

    @Test
	public void testCreateInjector_SingleModule() {
		final boolean[] configureCalled = new boolean[1];

		Module module = binder -> configureCalled[0] = true;

		Injector injector = DIBootstrap.createInjector(module);
		assertNotNull(injector);

		assertTrue(configureCalled[0]);
	}

    @Test
	public void testCreateInjector_MultiModule() {

		final boolean[] configureCalled = new boolean[2];

		Module module1 = binder -> configureCalled[0] = true;

		Module module2 = binder -> configureCalled[1] = true;

		Injector injector = DIBootstrap.createInjector(module1, module2);
		assertNotNull(injector);

		assertTrue(configureCalled[0]);
		assertTrue(configureCalled[1]);
	}
}
