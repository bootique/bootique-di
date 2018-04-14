
package io.bootique.di.spi;

import javax.inject.Inject;

import io.bootique.di.DIBootstrap;
import io.bootique.di.Injector;
import io.bootique.di.Key;
import io.bootique.di.Module;
import io.bootique.di.mock.MockImplementation1;
import io.bootique.di.mock.MockImplementation1Alt;
import io.bootique.di.mock.MockImplementation1Alt2;
import io.bootique.di.mock.MockInterface1;
import io.bootique.di.mock.MockInterface1Provider;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultInjectorBindingTest {

    @Test
    public void testClassBinding() {

        Module module = binder -> binder.bind(MockInterface1.class).to(MockImplementation1.class);

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface1 service = injector.getInstance(MockInterface1.class);
        assertNotNull(service);
        assertEquals("MyName", service.getName());
    }

    @Test
    public void testClassNamedBinding() {

        Module module = binder -> {
            binder.bind(MockInterface1.class).to(MockImplementation1.class);
            binder.bind(Key.get(MockInterface1.class, "abc")).to(
                    MockImplementation1Alt.class);
            binder.bind(Key.get(MockInterface1.class, "xyz")).to(
                    MockImplementation1Alt2.class);
        };

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface1 defaultObject = injector.getInstance(MockInterface1.class);
        assertNotNull(defaultObject);
        assertEquals("MyName", defaultObject.getName());

        MockInterface1 abcObject = injector.getInstance(Key.get(
                MockInterface1.class,
                "abc"));
        assertNotNull(abcObject);
        assertEquals("alt", abcObject.getName());

        MockInterface1 xyzObject = injector.getInstance(Key.get(
                MockInterface1.class,
                "xyz"));
        assertNotNull(xyzObject);
        assertEquals("alt2", xyzObject.getName());
    }

    @Test
    public void testProviderBinding() {
        Module module = binder -> binder
                .bind(MockInterface1.class)
                .toProvider(MockInterface1Provider.class);

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface1 service = injector.getInstance(MockInterface1.class);
        assertNotNull(service);
        assertEquals("MyName", service.getName());
    }

    @Test
    public void testInstanceBinding() {

        final MockImplementation1 instance = new MockImplementation1();

        Module module = binder -> binder.bind(MockInterface1.class).toInstance(instance);

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface1 service = injector.getInstance(MockInterface1.class);
        assertNotNull(service);
        assertSame(instance, service);
    }

    @Test
    public void testKeyBinding() {
        Module module = binder -> {
            binder.bind(MockInterface1.class).to(Key.get(MockImplementation1.class));
            binder.bind(MockImplementation1.class).to(MockImplementation1.class);
        };

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface1 service = injector.getInstance(MockInterface1.class);
        assertNotNull(service);
        assertEquals("MyName", service.getName());
    }

    @Test
    public void testClassReBinding() {

        Module module = binder -> {
            binder.bind(MockInterface1.class).to(MockImplementation1.class);
            binder.bind(MockInterface1.class).to(MockImplementation1Alt.class);
        };

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface1 service = injector.getInstance(MockInterface1.class);
        assertNotNull(service);
        assertEquals("alt", service.getName());
    }

    @Test
    public void testDirectImplementationBinding() {
        Module module = binder -> {
            binder.bind(Implementation1.class).withoutScope();
            binder.bind(Implementation2.class).inSingletonScope();
        };
        Injector injector = DIBootstrap.injectorBuilder(module).build();

        Implementation1 impl1 = injector.getInstance(Implementation1.class);
        assertNotNull(impl1);
        assertNotNull(impl1.implementation2);

        Implementation1 impl2 = injector.getInstance(Implementation1.class);
        assertNotNull(impl2);
        assertNotNull(impl2.implementation2);
        assertNotSame(impl1, impl2);
        assertSame(impl1.implementation2, impl2.implementation2);

    }

    public static class Implementation1 {
        @Inject
        Implementation2 implementation2;
    }

    public static class Implementation2 {
    }

}
