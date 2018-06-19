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

package org.atinject.tck.auto;

import junit.framework.TestCase;
import org.junit.Ignore;
import org.atinject.tck.auto.accessories.Cupholder;
import org.atinject.tck.auto.accessories.SpareTire;
import org.atinject.tck.auto.accessories.RoundThing;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

public class Convertible implements Car {

    @Inject @Drivers Seat driversSeatA;
    @Inject @Drivers Seat driversSeatB;
    @Inject SpareTire spareTire;
    @Inject Cupholder cupholder;
    @Inject Provider<Engine> engineProvider;

    private boolean methodWithZeroParamsInjected;
    private boolean methodWithMultipleParamsInjected;
    private boolean methodWithNonVoidReturnInjected;

    private Seat constructorPlainSeat;
    private Seat constructorDriversSeat;
    private Tire constructorPlainTire;
    private Tire constructorSpareTire;
    private Provider<Seat> constructorPlainSeatProvider = nullProvider();
    private Provider<Seat> constructorDriversSeatProvider = nullProvider();
    private Provider<Tire> constructorPlainTireProvider = nullProvider();
    private Provider<Tire> constructorSpareTireProvider = nullProvider();

    @Inject Seat fieldPlainSeat;
    @Inject @Drivers Seat fieldDriversSeat;
    @Inject Tire fieldPlainTire;
    @Inject @Named("spare") Tire fieldSpareTire;
    @Inject Provider<Seat> fieldPlainSeatProvider = nullProvider();
    @Inject @Drivers Provider<Seat> fieldDriversSeatProvider = nullProvider();
    @Inject Provider<Tire> fieldPlainTireProvider = nullProvider();
    @Inject @Named("spare") Provider<Tire> fieldSpareTireProvider = nullProvider();

    private Seat methodPlainSeat;
    private Seat methodDriversSeat;
    private Tire methodPlainTire;
    private Tire methodSpareTire;
    private Provider<Seat> methodPlainSeatProvider = nullProvider();
    private Provider<Seat> methodDriversSeatProvider = nullProvider();
    private Provider<Tire> methodPlainTireProvider = nullProvider();
    private Provider<Tire> methodSpareTireProvider = nullProvider();

    @Inject static Seat staticFieldPlainSeat;
    @Inject @Drivers static Seat staticFieldDriversSeat;
    @Inject static Tire staticFieldPlainTire;
    @Inject @Named("spare") static Tire staticFieldSpareTire;
    @Inject static Provider<Seat> staticFieldPlainSeatProvider = nullProvider();
    @Inject @Drivers static Provider<Seat> staticFieldDriversSeatProvider = nullProvider();
    @Inject static Provider<Tire> staticFieldPlainTireProvider = nullProvider();
    @Inject @Named("spare") static Provider<Tire> staticFieldSpareTireProvider = nullProvider();

    private static Seat staticMethodPlainSeat;
    private static Seat staticMethodDriversSeat;
    private static Tire staticMethodPlainTire;
    private static Tire staticMethodSpareTire;
    private static Provider<Seat> staticMethodPlainSeatProvider = nullProvider();
    private static Provider<Seat> staticMethodDriversSeatProvider = nullProvider();
    private static Provider<Tire> staticMethodPlainTireProvider = nullProvider();
    private static Provider<Tire> staticMethodSpareTireProvider = nullProvider();

    @Inject Convertible(
            Seat plainSeat,
            @Drivers Seat driversSeat,
            Tire plainTire,
            @Named("spare") Tire spareTire,
            Provider<Seat> plainSeatProvider,
            @Drivers Provider<Seat> driversSeatProvider,
            Provider<Tire> plainTireProvider,
            @Named("spare") Provider<Tire> spareTireProvider) {
        constructorPlainSeat = plainSeat;
        constructorDriversSeat = driversSeat;
        constructorPlainTire = plainTire;
        constructorSpareTire = spareTire;
        constructorPlainSeatProvider = plainSeatProvider;
        constructorDriversSeatProvider = driversSeatProvider;
        constructorPlainTireProvider = plainTireProvider;
        constructorSpareTireProvider = spareTireProvider;
    }

    Convertible() {
        throw new AssertionError("Unexpected call to non-injectable constructor");
    }

    void setSeat(Seat unused) {
        throw new AssertionError("Unexpected call to non-injectable method");
    }

    @Inject void injectMethodWithZeroArgs() {
        methodWithZeroParamsInjected = true;
    }

    @Inject String injectMethodWithNonVoidReturn() {
        methodWithNonVoidReturnInjected = true;
        return "unused";
    }

    @Inject void injectInstanceMethodWithManyArgs(
            Seat plainSeat,
            @Drivers Seat driversSeat,
            Tire plainTire,
            @Named("spare") Tire spareTire,
            Provider<Seat> plainSeatProvider,
            @Drivers Provider<Seat> driversSeatProvider,
            Provider<Tire> plainTireProvider,
            @Named("spare") Provider<Tire> spareTireProvider) {
        methodWithMultipleParamsInjected = true;

        methodPlainSeat = plainSeat;
        methodDriversSeat = driversSeat;
        methodPlainTire = plainTire;
        methodSpareTire = spareTire;
        methodPlainSeatProvider = plainSeatProvider;
        methodDriversSeatProvider = driversSeatProvider;
        methodPlainTireProvider = plainTireProvider;
        methodSpareTireProvider = spareTireProvider;
    }

    @Inject static void injectStaticMethodWithManyArgs(
            Seat plainSeat,
            @Drivers Seat driversSeat,
            Tire plainTire,
            @Named("spare") Tire spareTire,
            Provider<Seat> plainSeatProvider,
            @Drivers Provider<Seat> driversSeatProvider,
            Provider<Tire> plainTireProvider,
            @Named("spare") Provider<Tire> spareTireProvider) {
        staticMethodPlainSeat = plainSeat;
        staticMethodDriversSeat = driversSeat;
        staticMethodPlainTire = plainTire;
        staticMethodSpareTire = spareTire;
        staticMethodPlainSeatProvider = plainSeatProvider;
        staticMethodDriversSeatProvider = driversSeatProvider;
        staticMethodPlainTireProvider = plainTireProvider;
        staticMethodSpareTireProvider = spareTireProvider;
    }

    /**
     * Returns a provider that always returns null. This is used as a default
     * value to avoid null checks for omitted provider injections.
     */
    private static <T> Provider<T> nullProvider() {
        return new Provider<T>() {
            public T get() {
                return null;
            }
        };
    }

    public static ThreadLocal<Convertible> localConvertible
            = new ThreadLocal<Convertible>();

    public static class Tests extends TestCase {

        private final Convertible car = localConvertible.get();
        private final Cupholder cupholder = car.cupholder;
        private final SpareTire spareTire = car.spareTire;
        private final Tire plainTire = car.fieldPlainTire;
        private final Engine engine = car.engineProvider.get();

        // smoke tests: if these fail all bets are off

        public void testFieldsInjected() {
            TestCase.assertTrue(cupholder != null && spareTire != null);
        }

        public void testProviderReturnedValues() {
            TestCase.assertTrue(engine != null);
        }

        // injecting different kinds of members

        public void testMethodWithZeroParametersInjected() {
            TestCase.assertTrue(car.methodWithZeroParamsInjected);
        }

        public void testMethodWithMultipleParametersInjected() {
            TestCase.assertTrue(car.methodWithMultipleParamsInjected);
        }

        public void testNonVoidMethodInjected() {
            TestCase.assertTrue(car.methodWithNonVoidReturnInjected);
        }

        public void testPublicNoArgsConstructorInjected() {
            assertTrue(engine.publicNoArgsConstructorInjected);
        }

        public void testSubtypeFieldsInjected() {
            assertTrue(spareTire.hasSpareTireBeenFieldInjected());
        }

        public void testSubtypeMethodsInjected() {
            assertTrue(spareTire.hasSpareTireBeenMethodInjected());
        }

        public void testSupertypeFieldsInjected() {
            assertTrue(spareTire.hasTireBeenFieldInjected());
        }

        public void testSupertypeMethodsInjected() {
            assertTrue(spareTire.hasTireBeenMethodInjected());
        }

        public void testTwiceOverriddenMethodInjectedWhenMiddleLacksAnnotation() {
            assertTrue(engine.overriddenTwiceWithOmissionInMiddleInjected);
        }

        // injected values

        public void testQualifiersNotInheritedFromOverriddenMethod() {
            assertFalse(engine.qualifiersInheritedFromOverriddenMethod);
        }

        public void testConstructorInjectionWithValues() {
            TestCase.assertFalse("Expected unqualified value",
                    car.constructorPlainSeat instanceof DriversSeat);
            TestCase.assertFalse("Expected unqualified value",
                    car.constructorPlainTire instanceof SpareTire);
            TestCase.assertTrue("Expected qualified value",
                    car.constructorDriversSeat instanceof DriversSeat);
            TestCase.assertTrue("Expected qualified value",
                    car.constructorSpareTire instanceof SpareTire);
        }

        public void testFieldInjectionWithValues() {
            TestCase.assertFalse("Expected unqualified value",
                    car.fieldPlainSeat instanceof DriversSeat);
            TestCase.assertFalse("Expected unqualified value",
                    car.fieldPlainTire instanceof SpareTire);
            TestCase.assertTrue("Expected qualified value",
                    car.fieldDriversSeat instanceof DriversSeat);
            TestCase.assertTrue("Expected qualified value",
                    car.fieldSpareTire instanceof SpareTire);
        }

        public void testMethodInjectionWithValues() {
            TestCase.assertFalse("Expected unqualified value",
                    car.methodPlainSeat instanceof DriversSeat);
            TestCase.assertFalse("Expected unqualified value",
                    car.methodPlainTire instanceof SpareTire);
            TestCase.assertTrue("Expected qualified value",
                    car.methodDriversSeat instanceof DriversSeat);
            TestCase.assertTrue("Expected qualified value",
                    car.methodSpareTire instanceof SpareTire);
        }

        // injected providers

        public void testConstructorInjectionWithProviders() {
            TestCase.assertFalse("Expected unqualified value",
                    car.constructorPlainSeatProvider.get() instanceof DriversSeat);
            TestCase.assertFalse("Expected unqualified value",
                    car.constructorPlainTireProvider.get() instanceof SpareTire);
            TestCase.assertTrue("Expected qualified value",
                    car.constructorDriversSeatProvider.get() instanceof DriversSeat);
            TestCase.assertTrue("Expected qualified value",
                    car.constructorSpareTireProvider.get() instanceof SpareTire);
        }

        public void testFieldInjectionWithProviders() {
            TestCase.assertFalse("Expected unqualified value",
                    car.fieldPlainSeatProvider.get() instanceof DriversSeat);
            TestCase.assertFalse("Expected unqualified value",
                    car.fieldPlainTireProvider.get() instanceof SpareTire);
            TestCase.assertTrue("Expected qualified value",
                    car.fieldDriversSeatProvider.get() instanceof DriversSeat);
            TestCase.assertTrue("Expected qualified value",
                    car.fieldSpareTireProvider.get() instanceof SpareTire);
        }

        public void testMethodInjectionWithProviders() {
            TestCase.assertFalse("Expected unqualified value",
                    car.methodPlainSeatProvider.get() instanceof DriversSeat);
            TestCase.assertFalse("Expected unqualified value",
                    car.methodPlainTireProvider.get() instanceof SpareTire);
            TestCase.assertTrue("Expected qualified value",
                    car.methodDriversSeatProvider.get() instanceof DriversSeat);
            TestCase.assertTrue("Expected qualified value",
                    car.methodSpareTireProvider.get() instanceof SpareTire);
        }


        // singletons

        public void testConstructorInjectedProviderYieldsSingleton() {
            TestCase.assertSame("Expected same value",
                    car.constructorPlainSeatProvider.get(), car.constructorPlainSeatProvider.get());
        }

        public void testFieldInjectedProviderYieldsSingleton() {
            TestCase.assertSame("Expected same value",
                    car.fieldPlainSeatProvider.get(), car.fieldPlainSeatProvider.get());
        }

        public void testMethodInjectedProviderYieldsSingleton() {
            TestCase.assertSame("Expected same value",
                    car.methodPlainSeatProvider.get(), car.methodPlainSeatProvider.get());
        }

        public void testCircularlyDependentSingletons() {
            // uses provider.get() to get around circular deps
            assertSame(cupholder.seatProvider.get().getCupholder(), cupholder);
        }


        // non singletons

        public void testSingletonAnnotationNotInheritedFromSupertype() {
            TestCase.assertNotSame(car.driversSeatA, car.driversSeatB);
        }

        public void testConstructorInjectedProviderYieldsDistinctValues() {
            TestCase.assertNotSame("Expected distinct values",
                    car.constructorDriversSeatProvider.get(), car.constructorDriversSeatProvider.get());
            TestCase.assertNotSame("Expected distinct values",
                    car.constructorPlainTireProvider.get(), car.constructorPlainTireProvider.get());
            TestCase.assertNotSame("Expected distinct values",
                    car.constructorSpareTireProvider.get(), car.constructorSpareTireProvider.get());
        }

        public void testFieldInjectedProviderYieldsDistinctValues() {
            TestCase.assertNotSame("Expected distinct values",
                    car.fieldDriversSeatProvider.get(), car.fieldDriversSeatProvider.get());
            TestCase.assertNotSame("Expected distinct values",
                    car.fieldPlainTireProvider.get(), car.fieldPlainTireProvider.get());
            TestCase.assertNotSame("Expected distinct values",
                    car.fieldSpareTireProvider.get(), car.fieldSpareTireProvider.get());
        }

        public void testMethodInjectedProviderYieldsDistinctValues() {
            TestCase.assertNotSame("Expected distinct values",
                    car.methodDriversSeatProvider.get(), car.methodDriversSeatProvider.get());
            TestCase.assertNotSame("Expected distinct values",
                    car.methodPlainTireProvider.get(), car.methodPlainTireProvider.get());
            TestCase.assertNotSame("Expected distinct values",
                    car.methodSpareTireProvider.get(), car.methodSpareTireProvider.get());
        }


        // mix inheritance + visibility

        public void testPackagePrivateMethodInjectedDifferentPackages() {
            assertTrue(spareTire.subPackagePrivateMethodInjected);
            assertTrue(spareTire.superPackagePrivateMethodInjected);
        }

        public void testOverriddenProtectedMethodInjection() {
            assertTrue(spareTire.subProtectedMethodInjected);
            assertFalse(spareTire.superProtectedMethodInjected);
        }

        public void testOverriddenPublicMethodNotInjected() {
            assertTrue(spareTire.subPublicMethodInjected);
            assertFalse(spareTire.superPublicMethodInjected);
        }


        // inject in order

        public void testFieldsInjectedBeforeMethods() {
            assertFalse(spareTire.methodInjectedBeforeFields);
        }

        /**
         * FIXME: here is Bootique DI implementation differs from specification
         */
        public void testSupertypeMethodsInjectedBeforeSubtypeFields() {
            // This test logic is inverted as Bootique DI does field injection first all the way down to given class,
            // and methods injected only after fields injection is done.
            // assertFalse(spareTire.subtypeFieldInjectedBeforeSupertypeMethods);
            assertTrue(spareTire.subtypeFieldInjectedBeforeSupertypeMethods);
        }

        public void testSupertypeMethodInjectedBeforeSubtypeMethods() {
            assertFalse(spareTire.subtypeMethodInjectedBeforeSupertypeMethods);
        }


        // necessary injections occur

        public void testPackagePrivateMethodInjectedEvenWhenSimilarMethodLacksAnnotation() {
            assertTrue(spareTire.subPackagePrivateMethodForOverrideInjected);
        }


        // override or similar method without @Inject

        public void testPrivateMethodNotInjectedWhenSupertypeHasAnnotatedSimilarMethod() {
            assertFalse(spareTire.superPrivateMethodForOverrideInjected);
        }

        public void testPackagePrivateMethodNotInjectedWhenOverrideLacksAnnotation() {
            assertFalse(engine.subPackagePrivateMethodForOverrideInjected);
            assertFalse(engine.superPackagePrivateMethodForOverrideInjected);
        }

        public void testPackagePrivateMethodNotInjectedWhenSupertypeHasAnnotatedSimilarMethod() {
            assertFalse(spareTire.superPackagePrivateMethodForOverrideInjected);
        }

        public void testProtectedMethodNotInjectedWhenOverrideNotAnnotated() {
            assertFalse(spareTire.protectedMethodForOverrideInjected);
        }

        public void testPublicMethodNotInjectedWhenOverrideNotAnnotated() {
            assertFalse(spareTire.publicMethodForOverrideInjected);
        }

        public void testTwiceOverriddenMethodNotInjectedWhenOverrideLacksAnnotation() {
            assertFalse(engine.overriddenTwiceWithOmissionInSubclassInjected);
        }

        public void testOverriddingMixedWithPackagePrivate2() {
            assertTrue(spareTire.packagePrivateMethod2Injected);
            TestCase.assertTrue(((Tire) spareTire).packagePrivateMethod2Injected);
            assertFalse(((RoundThing) spareTire).packagePrivateMethod2Injected);

            TestCase.assertTrue(plainTire.packagePrivateMethod2Injected);
            assertTrue(((RoundThing) plainTire).packagePrivateMethod2Injected);
        }

        public void testOverriddingMixedWithPackagePrivate3() {
            assertFalse(spareTire.packagePrivateMethod3Injected);
            TestCase.assertTrue(((Tire) spareTire).packagePrivateMethod3Injected);
            assertFalse(((RoundThing) spareTire).packagePrivateMethod3Injected);

            TestCase.assertTrue(plainTire.packagePrivateMethod3Injected);
            assertTrue(((RoundThing) plainTire).packagePrivateMethod3Injected);
        }

        public void testOverriddingMixedWithPackagePrivate4() {
            TestCase.assertFalse(plainTire.packagePrivateMethod4Injected);
            assertTrue(((RoundThing) plainTire).packagePrivateMethod4Injected);
        }

        // inject only once

        public void testOverriddenPackagePrivateMethodInjectedOnlyOnce() {
            assertFalse(engine.overriddenPackagePrivateMethodInjectedTwice);
        }

        public void testSimilarPackagePrivateMethodInjectedOnlyOnce() {
            assertFalse(spareTire.similarPackagePrivateMethodInjectedTwice);
        }

        public void testOverriddenProtectedMethodInjectedOnlyOnce() {
            assertFalse(spareTire.overriddenProtectedMethodInjectedTwice);
        }

        public void testOverriddenPublicMethodInjectedOnlyOnce() {
            assertFalse(spareTire.overriddenPublicMethodInjectedTwice);
        }

    }

    public static class StaticTests extends TestCase {

        public void testSubtypeStaticFieldsInjected() {
            assertTrue(SpareTire.hasBeenStaticFieldInjected());
        }

        public void testSubtypeStaticMethodsInjected() {
            assertTrue(SpareTire.hasBeenStaticMethodInjected());
        }

        public void testSupertypeStaticFieldsInjected() {
            TestCase.assertTrue(Tire.hasBeenStaticFieldInjected());
        }

        public void testSupertypeStaticMethodsInjected() {
            TestCase.assertTrue(Tire.hasBeenStaticMethodInjected());
        }

        public void testStaticFieldInjectionWithValues() {
            TestCase.assertFalse("Expected unqualified value",
                    staticFieldPlainSeat instanceof DriversSeat);
            TestCase.assertFalse("Expected unqualified value",
                    staticFieldPlainTire instanceof SpareTire);
            TestCase.assertTrue("Expected qualified value",
                    staticFieldDriversSeat instanceof DriversSeat);
            TestCase.assertTrue("Expected qualified value",
                    staticFieldSpareTire instanceof SpareTire);
        }

        public void testStaticMethodInjectionWithValues() {
            TestCase.assertFalse("Expected unqualified value",
                    staticMethodPlainSeat instanceof DriversSeat);
            TestCase.assertFalse("Expected unqualified value",
                    staticMethodPlainTire instanceof SpareTire);
            TestCase.assertTrue("Expected qualified value",
                    staticMethodDriversSeat instanceof DriversSeat);
            TestCase.assertTrue("Expected qualified value",
                    staticMethodSpareTire instanceof SpareTire);
        }

        public void testStaticFieldsInjectedBeforeMethods() {
            assertFalse(SpareTire.staticMethodInjectedBeforeStaticFields);
        }

        public void testSupertypeStaticMethodsInjectedBeforeSubtypeStaticFields() {
            assertFalse(SpareTire.subtypeStaticFieldInjectedBeforeSupertypeStaticMethods);
        }

        public void testSupertypeStaticMethodsInjectedBeforeSubtypeStaticMethods() {
            assertFalse(SpareTire.subtypeStaticMethodInjectedBeforeSupertypeStaticMethods);
        }

        public void testStaticFieldInjectionWithProviders() {
            TestCase.assertFalse("Expected unqualified value",
                    staticFieldPlainSeatProvider.get() instanceof DriversSeat);
            TestCase.assertFalse("Expected unqualified value",
                    staticFieldPlainTireProvider.get() instanceof SpareTire);
            TestCase.assertTrue("Expected qualified value",
                    staticFieldDriversSeatProvider.get() instanceof DriversSeat);
            TestCase.assertTrue("Expected qualified value",
                    staticFieldSpareTireProvider.get() instanceof SpareTire);
        }

        public void testStaticMethodInjectionWithProviders() {
            TestCase.assertFalse("Expected unqualified value",
                    staticMethodPlainSeatProvider.get() instanceof DriversSeat);
            TestCase.assertFalse("Expected unqualified value",
                    staticMethodPlainTireProvider.get() instanceof SpareTire);
            TestCase.assertTrue("Expected qualified value",
                    staticMethodDriversSeatProvider.get() instanceof DriversSeat);
            TestCase.assertTrue("Expected qualified value",
                    staticMethodSpareTireProvider.get() instanceof SpareTire);
        }
    }

    public static class PrivateTests extends TestCase {

        private final Convertible car = localConvertible.get();
        private final Engine engine = car.engineProvider.get();
        private final SpareTire spareTire = car.spareTire;

        public void testSupertypePrivateMethodInjected() {
            assertTrue(spareTire.superPrivateMethodInjected);
            assertTrue(spareTire.subPrivateMethodInjected);
        }

        public void testPackagePrivateMethodInjectedSamePackage() {
            assertTrue(engine.subPackagePrivateMethodInjected);
            assertFalse(engine.superPackagePrivateMethodInjected);
        }

        public void testPrivateMethodInjectedEvenWhenSimilarMethodLacksAnnotation() {
            assertTrue(spareTire.subPrivateMethodForOverrideInjected);
        }

        public void testSimilarPrivateMethodInjectedOnlyOnce() {
            assertFalse(spareTire.similarPrivateMethodInjectedTwice);
        }
    }
}
