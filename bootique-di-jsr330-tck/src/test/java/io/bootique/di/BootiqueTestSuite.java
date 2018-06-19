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

package io.bootique.di;

import org.atinject.tck.Tck;
import org.atinject.tck.auto.Car;
import junit.framework.Test;
import org.atinject.tck.auto.Convertible;
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
 * Test suite provided by JSR-330 TCK
 *
 * One test is failing permanently and disabled,
 * see {@link Convertible.Tests#testSupertypeMethodsInjectedBeforeSubtypeFields}
 */
public class BootiqueTestSuite {
    
    public static Test suite() {
        Car car = createInjector().getInstance(Car.class);
        return Tck.testsFor(car, false, true);
    }
    
    private static Injector createInjector() {
        Module module = binder -> {
            binder.bind(Car.class).to(Convertible.class);
            binder.bind(Key.get(Seat.class, Drivers.class)).to(DriversSeat.class);
            binder.bind(Key.get(Tire.class, "spare")).to(SpareTire.class);
            binder.bind(Engine.class).to(V8Engine.class);
        };

        return DIBootstrap.injectorBuilder(module)
                .enableMethodInjection() // method injection disabled by default
                .enableDynamicBindings() // create objects on-demand
                .defaultNoScope()        // default scope is singleton
                .build();
    }

}
