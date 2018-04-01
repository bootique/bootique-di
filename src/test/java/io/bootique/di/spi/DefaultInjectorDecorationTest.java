
package io.bootique.di.spi;

import io.bootique.di.Module;
import io.bootique.di.mock.MockImplementation1;
import io.bootique.di.mock.MockInterface1;
import io.bootique.di.mock.MockInterface1_Decorator1;
import io.bootique.di.mock.MockInterface1_Decorator2;
import io.bootique.di.mock.MockInterface1_Decorator3;
import io.bootique.di.mock.MockInterface1_Decorator4;
import io.bootique.di.mock.MockInterface1_Decorator5;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DefaultInjectorDecorationTest {

    @Test
    public void testSingleDecorator_After() {

        Module module = binder -> {
            binder.bind(MockInterface1.class).to(MockImplementation1.class);
            binder.decorate(MockInterface1.class).after(MockInterface1_Decorator1.class);
        };

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface1 service = injector.getInstance(MockInterface1.class);
        assertNotNull(service);
        assertEquals("[MyName]", service.getName());
    }

    @Test
    public void testSingleDecorator_Before() {

        Module module = binder -> {
            binder.bind(MockInterface1.class).to(MockImplementation1.class);
            binder.decorate(MockInterface1.class).before(MockInterface1_Decorator1.class);
        };

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface1 service = injector.getInstance(MockInterface1.class);
        assertNotNull(service);
        assertEquals("[MyName]", service.getName());
    }

    @Test
    public void testDecoratorChain() {

        Module module = binder -> {
            binder.bind(MockInterface1.class).to(MockImplementation1.class);
            binder.decorate(MockInterface1.class).before(MockInterface1_Decorator1.class);
            binder.decorate(MockInterface1.class).before(MockInterface1_Decorator2.class);
            binder.decorate(MockInterface1.class).after(MockInterface1_Decorator3.class);

        };

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface1 service = injector.getInstance(MockInterface1.class);
        assertNotNull(service);
        assertEquals("<[{MyName}]>", service.getName());
    }

    @Test
    public void testSingleDecorator_Provider_ConstructorInjection() {

        Module module = binder -> {
            binder.bind(MockInterface1.class).to(MockImplementation1.class);
            binder.decorate(MockInterface1.class).before(MockInterface1_Decorator4.class);
        };

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface1 service = injector.getInstance(MockInterface1.class);
        assertNotNull(service);
        assertEquals("[4MyName4]", service.getName());
    }

    @Test
    public void testSingleDecorator_Provider_FieldInjection() {

        Module module = binder -> {
            binder.bind(MockInterface1.class).to(MockImplementation1.class);
            binder.decorate(MockInterface1.class).before(MockInterface1_Decorator5.class);
        };

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface1 service = injector.getInstance(MockInterface1.class);
        assertNotNull(service);
        assertEquals("[5MyName5]", service.getName());
    }
}
