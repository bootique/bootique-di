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

package org.atinject.tck;

import org.atinject.tck.auto.Car;
import org.atinject.tck.auto.Convertible;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.atinject.tck.auto.Drivers;
import org.atinject.tck.auto.DriversSeat;
import org.atinject.tck.auto.Engine;
import org.atinject.tck.auto.FuelTank;
import org.atinject.tck.auto.Seat;
import org.atinject.tck.auto.Tire;
import org.atinject.tck.auto.V8Engine;
import org.atinject.tck.auto.accessories.Cupholder;
import org.atinject.tck.auto.accessories.SpareTire;

/**
 * Manufactures the compatibility test suite. This TCK relies on
 * <a href="http://junit.org/">JUnit</a>. To integrate the TCK with your
 * injector, create a JUnit test suite class that passes an injected
 * {@link Car Car} instance to {@link #testsFor testsFor(Car)}:
 *
 * <pre>
 * import junit.framework.Test;
 * import org.atinject.tck.Tck;
 * import org.atinject.tck.auto.Car;
 *
 * public class MyTck {
 *   public static Test suite() {
 *     Car car = new MyInjector().getInstance(Car.class);
 *     return Tck.testsFor(car,
 *         true /* supportsStatic &#42;/,
 *         true /* supportsPrivate &#42;/);
 *   }
 * }</pre>
 *
 * <p>The static {@code suite} method that returns a {@code Test} is a JUnit
 * convention. Feel free to run the returned tests in other ways.
 *
 * <p>Configure the injector as follows:
 *
 * <ul>
 *   <li>{@link Car} is implemented by
 *       {@link Convertible Convertible}.
 *   <li>{@link Drivers @Drivers}
 *       {@link Seat Seat} is
 *       implemented by {@link DriversSeat DriversSeat}.
 *   <li>{@link Seat Seat} is
 *       implemented by {@link Seat Seat} itself, and
 *       {@link Tire Tire} by
 *       {@link Tire Tire} itself
 *       (not subclasses).
 *   <li>{@link Engine Engine} is implemented by
 *       {@link V8Engine V8Engine}.
 *   <li>{@link javax.inject.Named @Named("spare")}
 *       {@link Tire Tire} is implemented by
 *       {@link SpareTire SpareTire}.
 *   <li>The following classes may also be injected directly:
 *       {@link Cupholder Cupholder},
 *       {@link SpareTire SpareTire}, and
 *       {@link FuelTank FuelTank}.
 * </ul>
 *
 * <p>Static and private member injection support is optional, but if your
 * injector supports those features, it must pass the respective tests. If
 * static member injection is supported, the static members of the following
 * types shall also be injected once:
 * {@link Convertible Convertible},
 * {@link Tire Tire}, and
 * {@link SpareTire SpareTire}.
 *
 * <p>Use your favorite JUnit tool to run the tests. For example, you can use
 * your IDE or JUnit's command line runner:
 *
 * <pre>
 * java -cp javax.inject-tck.jar:junit.jar:myinjector.jar \
 *     junit.textui.TestRunner MyTck</pre>
 */
public class Tck {

    private Tck() {}

    /**
     * Constructs a JUnit test suite for the given {@link Car} instance.
     *
     * @param car to test
     * @param supportsStatic true if the injector supports static member
     *  injection
     * @param supportsPrivate true if the injector supports private member
     *  injection
     *
     * @throws NullPointerException if car is null
     * @throws ClassCastException if car doesn't extend
     *  {@link Convertible Convertible}
     */
    public static Test testsFor(Car car, boolean supportsStatic,
            boolean supportsPrivate) {
        if (car == null) {
            throw new NullPointerException("car");
        }

        if (!(car instanceof Convertible)) {
            throw new ClassCastException("car doesn't implement Convertible");
        }

        Convertible.localConvertible.set((Convertible) car);
        try {
            TestSuite suite = new TestSuite(Convertible.Tests.class);
            if (supportsStatic) {
                suite.addTestSuite(Convertible.StaticTests.class);
            }
            if (supportsPrivate) {
                suite.addTestSuite(Convertible.PrivateTests.class);
            }
            return suite;
        } finally {
            Convertible.localConvertible.remove();
        }
    }
}
