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

package io.bootique.di.docs;

import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import io.bootique.di.BQModule;
import io.bootique.di.Binder;
import io.bootique.di.DIBootstrap;
import io.bootique.di.Injector;
import io.bootique.di.Key;
import io.bootique.di.Provides;

public class Application {
    public static void main(String[] args) {
        DIBootstrap.createInjector();

        Injector injector = DIBootstrap
                .injectorBuilder(
                        binder -> binder.bind(Service.class)
                                    .to(MyService.class) // <!--1-->
                                    .inSingletonScope(),
                        binder -> binder.bind(Worker.class).to(MyWorker.class),
                        binder -> {
                            binder.bind(String.class, "1").toInstance("string_1");
                            binder.bind(String.class, "2").toProvider(StringProvider.class);

                            binder.bindSet(String.class)
                                    .add(Key.get(String.class, "1"))
                                    .add(Key.get(String.class, "2"));
                        }
                )
                .build();
        Worker worker = injector.getInstance(Worker.class);
        worker.doWork();
    }
}

interface Worker {
    void doWork();
}

interface Service {
    String getInfo();
}

class MyModule implements BQModule {

    @Override
    public void configure(Binder binder) {
        binder.bindSet(Service.class)
                .add(DefaultService.class)
                .add(Key.get(Service.class, "internal"))
                .addProvider(ServiceProvider.class);
    }

    @Provides
    @Named("internal")
    Service createInternalService() {
        return new MyService();
    }

    @Provides
    Worker createWorker(Set<Service> services, Set<String> set) {
        return new MyWorker(services.iterator().next(), set);
    }

}

class DefaultService implements Service {

    @Override
    public String getInfo() {
        return "test";
    }
}

class ServiceProvider implements Provider<Service> {
    @Override
    public Service get() {
        return new MyService();
    }
}

class StringProvider implements Provider<String> {

    @Override
    public String get() {
        return "string_2";
    }
}

class MyService implements Service {

    @Override
    public String getInfo() {
        return "Hello world!";
    }
}

class MyWorker implements Worker {
    private Service service;

    @Inject
    MyWorker(Service service, Set<String> set) {
        this.service = service;
        System.out.println(set);
    }

    public void doWork() {
        System.out.println(service.getInfo());
    }
}