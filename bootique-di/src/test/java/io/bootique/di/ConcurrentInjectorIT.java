/**
 *    Licensed to the ObjectStyle LLC under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ObjectStyle LLC licenses
 *  this file to you under the Apache License, Version 2.0 (the
 *  “License”); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.bootique.di;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConcurrentInjectorIT {

    @Test
    public void testListProvider_NoScope() throws Exception {
        Injector injector = DIBootstrap.createInjector(binder -> {
            binder.bindList(String.class).add("1").add("2").add("3").withoutScope();
        });

        parallelTest(10000, () ->
                assertEquals(3, injector.getInstance(Key.getListOf(String.class)).size()));
    }

    @Test
    public void testConstructorProvider_NoScope() throws Exception {
        Injector injector = DIBootstrap.createInjector(binder -> {
            binder.bindList(String.class).add("1").add("2").add("3").withoutScope();
            binder.bind(Foo.class).to(FooImpl.class).withoutScope();
        });

        parallelTest(5000, () ->
                assertEquals(4, injector.getInstance(Key.get(Foo.class)).getStrings().size()));
    }

    @Test
    public void testConstructorProvider_SingletonScope() throws Exception {
        Injector injector = DIBootstrap.createInjector(binder -> {
            binder.bindList(String.class).add("1").add("2").add("3");
            binder.bind(Foo.class).to(FooImplSleep.class);
        });

        parallelTest(1000, () ->
                assertEquals(4, injector.getInstance(Key.get(Foo.class)).getStrings().size()));
    }

    @Test
    public void testImplementationBinding() throws Exception {
        Injector injector = DIBootstrap.createInjector(binder -> {
            binder.bindList(String.class).add("1").add("2").add("3");
            binder.bind(FooImplSleep.class);
        });

        parallelTest(1000, () ->
                assertEquals(4, injector.getInstance(Key.get(FooImplSleep.class)).getStrings().size()));
    }

    @Test
    public void testDynamicBinding() throws Exception {
        Injector injector = DIBootstrap.injectorBuilder(binder -> {
            binder.bindList(String.class).add("1").add("2").add("3");
        }).enableDynamicBindings().build();

        parallelTest(1000, () ->
                assertEquals(4, injector.getInstance(Key.get(FooImplSleep.class)).getStrings().size()));
    }


    interface Foo {
        List<String> getStrings();
    }

    static class FooImpl implements Foo {

        List<String> strings;

        @Inject
        FooImpl(List<String> strings) {
            strings.add("4");
            this.strings = strings;
        }

        public List<String> getStrings() {
            return strings;
        }
    }

    static class FooImplSleep implements Foo {

        List<String> strings;

        @Inject
        FooImplSleep(List<String> strings) throws InterruptedException {
            strings.add("4");
            this.strings = strings;
            Thread.sleep(100);
        }

        public List<String> getStrings() {
            return strings;
        }
    }

    private void parallelTest(int iterations, Runnable action) throws Exception {
        parallelTest(4, iterations, action);
    }

    private void parallelTest(int threads, int iterations, Runnable action) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(1);

        Runnable runner = () -> {
            try {
                latch.await();
                for (int i = 0; i < iterations; i++) {
                    action.run();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("Latch wait interrupted");
            }
        };

        Future<?>[] futures = new Future[threads];
        for(int i=0; i<threads; i++) {
            futures[i] = executor.submit(runner);
        }

        latch.countDown();

        for(int i=0; i<threads; i++) {
            futures[i].get();
        }
    }

}
