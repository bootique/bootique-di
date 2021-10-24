/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique.di.tck;

import io.bootique.di.BQModule;
import io.bootique.di.DIBootstrap;
import io.bootique.di.Injector;
import io.bootique.di.tck.accessories.Cupholder;
import io.bootique.di.tck.accessories.RoundThing;
import io.bootique.di.tck.accessories.SpareTire;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class InjectorTest {

    private static Convertible car;

    @BeforeAll
    public static void beforeAll() {
        InjectorTest.car = (Convertible) createInjector().getInstance(Car.class);
    }

    private static Injector createInjector() {
        BQModule module = binder -> {
            binder.bind(Car.class).to(Convertible.class);
            binder.bind(Seat.class, Drivers.class).to(DriversSeat.class);
            binder.bind(Tire.class, "spare").to(SpareTire.class);
            binder.bind(Engine.class).to(V8Engine.class);
        };

        return DIBootstrap.injectorBuilder(module)
                .enableMethodInjection() // method injection disabled by default
                .build();
    }

    @Nested
    class Tests {

        private final Cupholder cupholder = car.cupholder;
        private final SpareTire spareTire = car.spareTire;
        private final Tire plainTire = car.fieldPlainTire;
        private final Engine engine = car.engineProvider.get();

        // smoke tests: if these fail all bets are off

        @Test
        public void testFieldsInjected() {
            assertTrue(cupholder != null && spareTire != null);
        }

        @Test
        public void testProviderReturnedValues() {
            assertTrue(engine != null);
        }

        // injecting different kinds of members

        @Test
        public void testMethodWithZeroParametersInjected() {
            assertTrue(car.fotTest_methodWithZeroParamsInjected());
        }

        @Test
        public void testMethodWithMultipleParametersInjected() {
            assertTrue(car.forTest_methodWithMultipleParamsInjected());
        }

        @Test
        public void testNonVoidMethodInjected() {
            assertTrue(car.forTest_methodWithNonVoidReturnInjected());
        }

        @Test
        public void testPublicNoArgsConstructorInjected() {
            assertTrue(engine.publicNoArgsConstructorInjected);
        }

        @Test
        public void testSubtypeFieldsInjected() {
            assertTrue(spareTire.hasSpareTireBeenFieldInjected());
        }

        @Test
        public void testSubtypeMethodsInjected() {
            assertTrue(spareTire.hasSpareTireBeenMethodInjected());
        }

        @Test
        public void testSupertypeFieldsInjected() {
            assertTrue(spareTire.hasTireBeenFieldInjected());
        }

        @Test
        public void testSupertypeMethodsInjected() {
            assertTrue(spareTire.hasTireBeenMethodInjected());
        }

        @Test
        public void testTwiceOverriddenMethodInjectedWhenMiddleLacksAnnotation() {
            assertTrue(engine.overriddenTwiceWithOmissionInMiddleInjected);
        }

        // injected values

        @Test
        public void testQualifiersNotInheritedFromOverriddenMethod() {
            assertFalse(engine.qualifiersInheritedFromOverriddenMethod);
        }

        @Test
        public void testConstructorInjectionWithValues() {
            assertFalse(car.forTest_constructorPlainSeat() instanceof DriversSeat, "Expected unqualified value");
            assertFalse(car.forTest_constructorPlainTire() instanceof SpareTire, "Expected unqualified value");
            assertTrue(car.forTest_constructorDriversSeat() instanceof DriversSeat, "Expected qualified value");
            assertTrue(car.forTest_constructorSpareTire() instanceof SpareTire, "Expected qualified value");
        }

        @Test
        public void testFieldInjectionWithValues() {
            assertFalse(car.fieldPlainSeat instanceof DriversSeat, "Expected unqualified value");
            assertFalse(car.fieldPlainTire instanceof SpareTire, "Expected unqualified value");
            assertTrue(car.fieldDriversSeat instanceof DriversSeat, "Expected qualified value");
            assertTrue(car.fieldSpareTire instanceof SpareTire, "Expected qualified value");
        }

        @Test
        public void testMethodInjectionWithValues() {
            assertFalse(car.forTest_methodPlainSeat() instanceof DriversSeat, "Expected unqualified value");
            assertFalse(car.forTest_methodPlainTire() instanceof SpareTire, "Expected unqualified value");
            assertTrue(car.forTest_methodDriversSeat() instanceof DriversSeat, "Expected qualified value");
            assertTrue(car.forTest_methodSpareTire() instanceof SpareTire, "Expected qualified value");
        }

        // injected providers

        @Test
        public void testConstructorInjectionWithProviders() {
            assertFalse(car.forTest_constructorPlainSeatProvider().get() instanceof DriversSeat, "Expected unqualified value");
            assertFalse(car.forTest_constructorPlainTireProvider().get() instanceof SpareTire, "Expected unqualified value");
            assertTrue(car.forTest_constructorDriversSeatProvider().get() instanceof DriversSeat, "Expected qualified value");
            assertTrue(car.forTest_constructorSpareTireProvider().get() instanceof SpareTire, "Expected qualified value");
        }

        @Test
        public void testFieldInjectionWithProviders() {
            assertFalse(car.fieldPlainSeatProvider.get() instanceof DriversSeat, "Expected unqualified value");
            assertFalse(car.fieldPlainTireProvider.get() instanceof SpareTire, "Expected unqualified value");
            assertTrue(car.fieldDriversSeatProvider.get() instanceof DriversSeat, "Expected qualified value");
            assertTrue(car.fieldSpareTireProvider.get() instanceof SpareTire, "Expected qualified value");
        }

        @Test
        public void testMethodInjectionWithProviders() {
            assertFalse(car.forTest_methodPlainSeatProvider().get() instanceof DriversSeat, "Expected unqualified value");
            assertFalse(car.forTest_methodPlainTireProvider().get() instanceof SpareTire, "Expected unqualified value");
            assertTrue(car.forTest_methodDriversSeatProvider().get() instanceof DriversSeat, "Expected qualified value");
            assertTrue(car.forTest_methodSpareTireProvider().get() instanceof SpareTire, "Expected qualified value");
        }


        // singletons
        @Test
        public void testConstructorInjectedProviderYieldsSingleton() {
            assertSame(car.forTest_constructorPlainSeatProvider().get(), car.forTest_constructorPlainSeatProvider().get());
        }

        @Test
        public void testFieldInjectedProviderYieldsSingleton() {
            assertSame(car.fieldPlainSeatProvider.get(), car.fieldPlainSeatProvider.get());
        }

        @Test
        public void testMethodInjectedProviderYieldsSingleton() {
            assertSame(car.forTest_methodPlainSeatProvider().get(), car.forTest_methodPlainSeatProvider().get());
        }

        @Test
        public void testCircularlyDependentSingletons() {
            // uses provider.get() to get around circular deps
            assertSame(cupholder.seatProvider.get().getCupholder(), cupholder);
        }


        // non singletons

        @Test
        public void testSingletonAnnotationNotInheritedFromSupertype() {
            assertNotSame(car.driversSeatA, car.driversSeatB);
        }

        @Test
        public void testConstructorInjectedProviderYieldsDistinctValues() {
            assertNotSame(car.forTest_constructorDriversSeatProvider().get(), car.forTest_constructorDriversSeatProvider().get(), "Expected distinct values");
            assertNotSame(car.forTest_constructorPlainTireProvider().get(), car.forTest_constructorPlainTireProvider().get(), "Expected distinct values");
            assertNotSame(car.forTest_constructorSpareTireProvider().get(), car.forTest_constructorSpareTireProvider().get(), "Expected distinct values");
        }

        @Test
        public void testFieldInjectedProviderYieldsDistinctValues() {
            assertNotSame(car.fieldDriversSeatProvider.get(), car.fieldDriversSeatProvider.get(), "Expected distinct values");
            assertNotSame(car.fieldPlainTireProvider.get(), car.fieldPlainTireProvider.get(), "Expected distinct values");
            assertNotSame(car.fieldSpareTireProvider.get(), car.fieldSpareTireProvider.get(), "Expected distinct values");
        }

        @Test
        public void testMethodInjectedProviderYieldsDistinctValues() {
            assertNotSame(car.forTest_methodDriversSeatProvider().get(), car.forTest_methodDriversSeatProvider().get(), "Expected distinct values");
            assertNotSame(car.forTest_methodPlainTireProvider().get(), car.forTest_methodPlainTireProvider().get(), "Expected distinct values");
            assertNotSame(car.forTest_methodSpareTireProvider().get(), car.forTest_methodSpareTireProvider().get(), "Expected distinct values");
        }


        // mix inheritance + visibility
        @Test
        public void testPackagePrivateMethodInjectedDifferentPackages() {
            assertTrue(spareTire.subPackagePrivateMethodInjected);
            assertTrue(spareTire.superPackagePrivateMethodInjected);
        }

        @Test
        public void testOverriddenProtectedMethodInjection() {
            assertTrue(spareTire.subProtectedMethodInjected);
            assertFalse(spareTire.superProtectedMethodInjected);
        }

        @Test
        public void testOverriddenPublicMethodNotInjected() {
            assertTrue(spareTire.subPublicMethodInjected);
            assertFalse(spareTire.superPublicMethodInjected);
        }


        // inject in order

        @Test
        public void testFieldsInjectedBeforeMethods() {
            assertFalse(spareTire.methodInjectedBeforeFields);
        }

        /**
         * FIXME: here is Bootique DI implementation differs from specification
         */
        @Test
        @Disabled("FIXME: here is Bootique DI implementation differs from specification")
        public void testSupertypeMethodsInjectedBeforeSubtypeFields() {
            // This test logic is inverted as Bootique DI does field injection first all the way down to given class,
            // and methods injected only after fields injection is done.
            // assertFalse(spareTire.subtypeFieldInjectedBeforeSupertypeMethods);
            assertTrue(spareTire.subtypeFieldInjectedBeforeSupertypeMethods);
        }

        @Test
        public void testSupertypeMethodInjectedBeforeSubtypeMethods() {
            assertFalse(spareTire.subtypeMethodInjectedBeforeSupertypeMethods);
        }


        // necessary injections occur
        @Test
        public void testPackagePrivateMethodInjectedEvenWhenSimilarMethodLacksAnnotation() {
            assertTrue(spareTire.subPackagePrivateMethodForOverrideInjected);
        }


        // override or similar method without @Inject
        @Test
        public void testPrivateMethodNotInjectedWhenSupertypeHasAnnotatedSimilarMethod() {
            assertFalse(spareTire.superPrivateMethodForOverrideInjected);
        }

        @Test
        public void testPackagePrivateMethodNotInjectedWhenOverrideLacksAnnotation() {
            assertFalse(engine.subPackagePrivateMethodForOverrideInjected);
            assertFalse(engine.superPackagePrivateMethodForOverrideInjected);
        }

        @Test
        public void testPackagePrivateMethodNotInjectedWhenSupertypeHasAnnotatedSimilarMethod() {
            assertFalse(spareTire.superPackagePrivateMethodForOverrideInjected);
        }

        @Test
        public void testProtectedMethodNotInjectedWhenOverrideNotAnnotated() {
            assertFalse(spareTire.protectedMethodForOverrideInjected);
        }

        @Test
        public void testPublicMethodNotInjectedWhenOverrideNotAnnotated() {
            assertFalse(spareTire.publicMethodForOverrideInjected);
        }

        @Test
        public void testTwiceOverriddenMethodNotInjectedWhenOverrideLacksAnnotation() {
            assertFalse(engine.overriddenTwiceWithOmissionInSubclassInjected);
        }

        @Test
        public void testOverriddingMixedWithPackagePrivate2() {
            assertTrue(spareTire.packagePrivateMethod2Injected);
            assertTrue(((Tire) spareTire).packagePrivateMethod2Injected);
            Assertions.assertFalse(((RoundThing) spareTire).packagePrivateMethod2Injected);

            assertTrue(plainTire.packagePrivateMethod2Injected);
            assertTrue(((RoundThing) plainTire).packagePrivateMethod2Injected);
        }

        @Test
        public void testOverriddingMixedWithPackagePrivate3() {
            assertFalse(spareTire.packagePrivateMethod3Injected);
            assertTrue(((Tire) spareTire).packagePrivateMethod3Injected);
            assertFalse(((RoundThing) spareTire).packagePrivateMethod3Injected);

            assertTrue(plainTire.packagePrivateMethod3Injected);
            assertTrue(((RoundThing) plainTire).packagePrivateMethod3Injected);
        }

        @Test
        public void testOverriddingMixedWithPackagePrivate4() {
            assertFalse(plainTire.packagePrivateMethod4Injected);
            assertTrue(((RoundThing) plainTire).packagePrivateMethod4Injected);
        }

        // inject only once
        @Test
        public void testOverriddenPackagePrivateMethodInjectedOnlyOnce() {
            assertFalse(engine.overriddenPackagePrivateMethodInjectedTwice);
        }

        @Test
        public void testSimilarPackagePrivateMethodInjectedOnlyOnce() {
            assertFalse(spareTire.similarPackagePrivateMethodInjectedTwice);
        }

        @Test
        public void testOverriddenProtectedMethodInjectedOnlyOnce() {
            assertFalse(spareTire.overriddenProtectedMethodInjectedTwice);
        }

        @Test
        public void testOverriddenPublicMethodInjectedOnlyOnce() {
            assertFalse(spareTire.overriddenPublicMethodInjectedTwice);
        }
    }

    @Nested
    class PrivateTests {

        private final Engine engine = car.engineProvider.get();
        private final SpareTire spareTire = car.spareTire;

        @Test
        public void testSupertypePrivateMethodInjected() {
            assertTrue(spareTire.superPrivateMethodInjected);
            assertTrue(spareTire.subPrivateMethodInjected);
        }

        @Test
        public void testPackagePrivateMethodInjectedSamePackage() {
            assertTrue(engine.subPackagePrivateMethodInjected);
            assertFalse(engine.superPackagePrivateMethodInjected);
        }

        @Test
        public void testPrivateMethodInjectedEvenWhenSimilarMethodLacksAnnotation() {
            assertTrue(spareTire.subPrivateMethodForOverrideInjected);
        }

        @Test
        public void testSimilarPrivateMethodInjectedOnlyOnce() {
            assertFalse(spareTire.similarPrivateMethodInjectedTwice);
        }
    }
}
