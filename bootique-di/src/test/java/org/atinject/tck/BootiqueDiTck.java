package org.atinject.tck;

import io.bootique.di.DIBootstrap;
import io.bootique.di.Injector;
import io.bootique.di.Key;
import io.bootique.di.Module;
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

public class BootiqueDiTck {

    public static Test suite() {
        Car car = createInjector().getInstance(Car.class);
        return Tck.testsFor(car, false, true);
    }

    private static Injector createInjector() {
        Module module = binder -> {
            binder.bind(Car.class).to(Convertible.class);

            binder.bind(Seat.class).to(Seat.class);
            binder.bind(Key.get(Seat.class, Drivers.class)).to(DriversSeat.class);

            binder.bind(Tire.class).to(Tire.class);
            binder.bind(Key.get(Tire.class, "spare")).to(SpareTire.class);

            binder.bind(Engine.class).to(V8Engine.class);

            binder.bind(Cupholder.class).to(Cupholder.class);
            binder.bind(SpareTire.class).to(SpareTire.class);
            binder.bind(FuelTank.class).to(FuelTank.class);
        };

        return DIBootstrap.injectorBuilder(module)
                .enableMethodInjection()
                .defaultNoScope()
                .build();
    }

}
