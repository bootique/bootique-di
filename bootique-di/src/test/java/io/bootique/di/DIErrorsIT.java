package io.bootique.di;

import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.Test;

import static org.junit.Assert.*;

public class DIErrorsIT {

    @Test
    public void testLongInjectionChainFailure() {

        // deep injection error:
        // instantiate foo -> inject field bar
        //      -> bar provider -> inject constructor arg baz
        //      -> baz provider method -> inject arg QuuxConsumer
        //      -> QuuxConsumer -> inject field quux
        //      -> Quux implementation -> inject method arg Map<>
        //      -> Map provider -> resolve qux object
        //      -> qux provider method -> null
        //          -> should throw
        Injector injector = DIBootstrap.injectorBuilder(new TestModule()).enableMethodInjection().build();

        // check injection trace
        try {
            injector.getInstance(Foo.class);
            fail("Should throw DIRuntimeException");
        } catch (DIRuntimeException ex) {
            String message = ex.getOriginalMessage();
            String fullMessage = ex.getMessage();
            assertTrue(message, message.contains("returned NULL instance"));
            assertTrue(fullMessage, fullMessage.contains("returned NULL instance"));
            assertTrue(fullMessage, fullMessage.contains("Invoking provider method 'createQux()' on module 'io.bootique.di.DIErrorsIT$TestModule'"));
            assertTrue(fullMessage, fullMessage.contains("Injecting field 'bar' of class io.bootique.di.DIErrorsIT$FooImpl"));

            InjectionTraceElement[] traceElements = ex.getInjectionTrace();
            assertEquals(7, traceElements.length);
            assertEquals(Key.get(Qux.class), traceElements[0].getBindingKey());
            assertEquals(Key.getMapOf(String.class, Object.class), traceElements[1].getBindingKey());
            assertEquals(Key.get(Foo.class), traceElements[6].getBindingKey());
        }

        // check that trace is clean for second exception
        try {
            injector.getInstance(Qux.class);
            fail("Should throw DIRuntimeExceptio");
        } catch (DIRuntimeException ex) {
            String message = ex.getOriginalMessage();
            String fullMessage = ex.getMessage();
            assertTrue(message, message.contains("returned NULL instance"));
            assertTrue(fullMessage, fullMessage.contains("returned NULL instance"));
            assertTrue(fullMessage, fullMessage.contains("Invoking provider method 'createQux()' on module 'io.bootique.di.DIErrorsIT$TestModule'"));

            InjectionTraceElement[] traceElements = ex.getInjectionTrace();
            assertEquals(1, traceElements.length);
            assertEquals(Key.get(Qux.class), traceElements[0].getBindingKey());
        }
    }

    @Test
    public void testListCycleError() {

        Injector injector = DIBootstrap.createInjector(binder -> {
            binder.bind(Bar.class).to(BarImpl2.class);
            binder.bindList(Foo.class).add(FooImpl2.class)
                    .addAfter(FooImpl3.class, FooImpl2.class)
                    .addAfter(FooImpl4.class, FooImpl3.class)
                    .insertBefore(FooImpl4.class, FooImpl2.class)
                    .addAfter(FooImpl5.class, FooImpl4.class);
        });

        try {
            injector.getInstance(Bar.class);
            fail("Should throw DIRuntimeExceptio");
        } catch (DIRuntimeException ex) {
            String message = ex.getOriginalMessage();
            String fullMessage = ex.getMessage();
            assertTrue(message, message.contains("Cycle detected in list"));
            assertTrue(fullMessage, fullMessage.contains("Cycle detected in list"));
            assertTrue(fullMessage, fullMessage.contains("Sorting list elements"));

            InjectionTraceElement[] traceElements = ex.getInjectionTrace();
            assertEquals(2, traceElements.length);
            assertEquals(Key.getListOf(Foo.class), traceElements[0].getBindingKey());
            assertEquals(Key.get(Bar.class), traceElements[1].getBindingKey());
        }

    }

    private static class TestModule extends BaseModule {
        @Override
        public void configure(Binder binder) {
            binder.bind(Foo.class).to(FooImpl.class);
            binder.bind(Bar.class).toProvider(BarProvider.class);
            binder.bindMap(String.class, Object.class)
                    .put("key1", new Object())
                    .put("key2", Key.get(Qux.class));
            binder.bind(Quux.class).to(QuuxImpl.class);
            binder.bind(QuuxConsumer.class);
        }

        @Provides
        Baz createBaz(QuuxConsumer consumer) {
            return new Baz() {};
        }

        @Provides
        static Qux createQux() {
            return null;
        }
    }

    private static class QuuxImpl implements Quux {
        @Inject
        void setMap(Map<String, Object> map) {
        }
    }

    private static class FooImpl implements Foo {
        @Inject
        private Bar bar;
    }

    private static class QuuxConsumer {
        @Inject
        Quux quux;
    }

    private static class BarProvider implements Provider<Bar> {
        @Inject
        BarProvider(Baz baz) {
        }

        @Override
        public Bar get() {
            return new Bar() {};
        }
    }

    private static class FooImpl2 implements Foo {}
    private static class FooImpl3 implements Foo {}
    private static class FooImpl4 implements Foo {}

    private static class FooImpl5 implements Foo {
        @Inject
        Baz baz;
    }

    private static class BarImpl2 implements Bar {
        @Inject
        List<Foo> fooList;
    }

    interface Foo  {}
    interface Bar  {}
    interface Baz  {}
    interface Qux  {}
    interface Quux {}
}
