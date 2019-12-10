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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class SetTypesIT {

    @Test
    public void testByKeyAndValueTypeMapInjection() {
        Injector injector = DIBootstrap.createInjector(serviceModule1, b -> {
            b.bindSet(Integer.class).add(1).add(2);
            b.bindSet(String.class).add("3").add("4");
        });

        assertSetContent(injector);
    }

    @Test
    public void testByGenericTypeMapInjection() {
        Injector injector = DIBootstrap.createInjector(serviceModule1, b -> {
            b.bindSet(TypeLiteral.of(Integer.class)).add(1).add(2);
            b.bindSet(TypeLiteral.of(String.class)).add("3").add("4");
        });

        assertSetContent(injector);
    }

    @Test
    public void testDirectMapInjection() {
        Injector injector = DIBootstrap.createInjector(serviceModule1, b -> {
            Set<Integer> integerMap = new HashSet<>();
            integerMap.add(1);
            integerMap.add(2);

            Set<String> stringMap = new HashSet<>();
            stringMap.add("3");
            stringMap.add("4");

            b.bind(Key.get(new TypeLiteral<Set<Integer>>() {})).toInstance(integerMap);
            b.bind(Key.get(new TypeLiteral<Set<String>>() {})).toInstance(stringMap);
        });

        assertSetContent(injector);
    }

    @Test
    public void testProviderMapInjection() {
        Injector injector = DIBootstrap.createInjector(serviceModule1, new SetProviderModule());
        assertSetContent(injector);
    }

    @Test
    public void testWildcardMapDirectInjection() {
        Injector injector = DIBootstrap.createInjector(b -> {
            b.bind(Service.class).to(Service_Impl2.class);
            b.bindSet(new TypeLiteral<List<? extends Number>>() {})
                    .add(Arrays.asList(1, 2, 3));
        });

        Service service = injector.getInstance(Service.class);

        assertThat(service, instanceOf(Service_Impl2.class));

        Service_Impl2 impl = (Service_Impl2) service;
        assertThat(impl.getSet(), hasItem(Arrays.asList(1, 2, 3)));
    }

    @Test
    public void testWildcardMapProvider() {
        Injector injector = DIBootstrap.createInjector(new SetProviderModule(),
                b -> b.bind(Service.class).to(Service_Impl2.class));

        Service service = injector.getInstance(Service.class);

        assertThat(service, instanceOf(Service_Impl2.class));

        Service_Impl2 impl = (Service_Impl2) service;
        assertThat(impl.getSet(), hasItem(Arrays.asList(1, 2, 3)));
    }

    @Test(expected = DIRuntimeException.class)
    public void testDuplicateValue() {
        Injector injector = DIBootstrap.createInjector(b ->
                b.bindSet(TypeLiteral.of(Integer.class)).add(1).add(2).add(2));

        injector.getInstance(Key.get(TypeLiteral.setOf(Integer.class)));
    }

    @Test
    public void testAddType() {
        Injector injector = DIBootstrap.createInjector(
                new SetProviderModule(),
                b -> b.bindSet(Service.class).add(Service_Impl1.class).add(Service_Impl2.class)
        );
        Set<Service> services = injector.getInstance(Key.getSetOf(Service.class));

        assertEquals(2, services.size());
        for(Service service : services) {
            assertThat(service, anyOf(instanceOf(Service_Impl1.class), instanceOf(Service_Impl2.class)));
        }
    }

    @Test
    public void testAddAllBinding() {
        Injector injector = DIBootstrap.createInjector(b ->
                b.bindSet(TypeLiteral.of(Integer.class)).add(1).addAll(Arrays.asList(2,3,4)).add(5));

        Set<Integer> set = injector.getInstance(Key.get(TypeLiteral.setOf(Integer.class)));
        assertThat(set, hasItems(1,2,3,4,5));
    }

    @Test
    public void testContinueBinding() {
        Injector injector = DIBootstrap.createInjector(
                b -> b.bindSet(TypeLiteral.of(Integer.class)).add(1).add(2),
                b -> b.bindSet(TypeLiteral.of(Integer.class)).add(3).add(4)
        );

        Set<Integer> set = injector.getInstance(Key.get(TypeLiteral.setOf(Integer.class)));
        assertThat(set, hasItems(1,2,3,4));
    }

    @Test
    public void testAddKey() {
        Injector injector = DIBootstrap.createInjector(b -> {
            b.bind(Key.get(Integer.class, "1")).toInstance(1);
            b.bind(Key.get(Integer.class, "2")).toInstance(2);
            b.bindSet(Integer.class)
                    .add(Key.get(Integer.class, "1"))
                    .add(Key.get(Integer.class, "2"));
        });

        Set<Integer> set = injector.getInstance(Key.getSetOf(Integer.class));
        assertEquals(2, set.size());
        assertThat(set, hasItems(1,2));
    }

    private void assertSetContent(Injector injector) {
        Service service = injector.getInstance(Service.class);

        assertThat(service, instanceOf(Service_Impl1.class));

        Service_Impl1 impl = (Service_Impl1) service;

        assertThat(impl.getIntegerSet(), hasItems(1, 2));
        assertThat(impl.getStringSet(), hasItems("3", "4"));
    }

    private static final BQModule serviceModule1 = b -> b.bind(Service.class).to(Service_Impl1.class);

    interface Service {
    }

    private static class Service_Impl1 implements Service {
        @Inject
        private Set<String> stringSet;
        @Inject
        private Set<Integer> integerSet;

        Set<Integer> getIntegerSet() {
            return integerSet;
        }

        Set<String> getStringSet() {
            return stringSet;
        }
    }

    private static class Service_Impl2 implements Service {
        @Inject
        private Set<List<? extends Number>> set;

        Set<List<? extends Number>> getSet() {
            return set;
        }
    }

    public static class SetProviderModule extends BaseModule {

        @Provides
        public Set<Integer> createIntegerSet() {
            Set<Integer> integerSet = new HashSet<>();
            integerSet.add(1);
            integerSet.add(2);
            return integerSet;
        }

        @Provides
        public Set<String> createStringSet() {
            Set<String> stringSet = new HashSet<>();
            stringSet.add("3");
            stringSet.add("4");
            return stringSet;
        }

        @Provides
        public Set<List<? extends Number>> createListSet() {
            Set<List<? extends Number>> listSet = new HashSet<>();
            listSet.add(Arrays.asList(1, 2, 3));
            return listSet;
        }
    }

}
