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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class MapTypesIT {

    @Test
    public void testByKeyAndValueTypeMapInjection() {
        Injector injector = DIBootstrap.createInjector(serviceModule1, b -> {
            b.bindMap(Integer.class, String.class).put(1, "1").put(2, "2");
            b.bindMap(String.class, String.class).put("3", "3").put("4", "4");
        });

        assertMapContent(injector);
    }

    @Test
    public void testByGenericTypeMapInjection() {
        Injector injector = DIBootstrap.createInjector(serviceModule1, b -> {
            b.bindMap(TypeLiteral.of(Integer.class), TypeLiteral.of(String.class)).put(1, "1").put(2, "2");
            b.bindMap(TypeLiteral.of(String.class), TypeLiteral.of(String.class)).put("3", "3").put("4", "4");
        });

        assertMapContent(injector);
    }

    @Test
    public void testDirectMapInjection() {
        final Map<Integer, String> integerMap = new HashMap<>();
        integerMap.put(1, "1");
        integerMap.put(2, "2");

        final Map<String, String> stringMap = new HashMap<>();
        stringMap.put("3", "3");
        stringMap.put("4", "4");

        Injector injector = DIBootstrap.createInjector(serviceModule1, b -> {
            b.bind(Key.get(new TypeLiteral<Map<Integer, String>>(){})).toInstance(integerMap);
            b.bind(Key.get(new TypeLiteral<Map<String, String>>(){})).toInstance(stringMap);
        });

        assertMapContent(injector);
    }

    @Test
    public void testProviderMapInjection() {
        Injector injector = DIBootstrap.createInjector(serviceModule1, new MapProviderModule());
        assertMapContent(injector);
    }

    @Test
    public void testWildcardMapDirectInjection() {
        Injector injector = DIBootstrap.createInjector(b -> {
            b.bind(Service.class).to(Service_Impl2.class);
            b.bindMap(new TypeLiteral<String>(){}, new TypeLiteral<List<? extends Number>>(){})
                    .put("1", Arrays.asList(1, 2, 3));
        });

        Service service = injector.getInstance(Service.class);

        assertThat(service, instanceOf(Service_Impl2.class));

        Service_Impl2 impl = (Service_Impl2)service;
        assertArrayEquals(new Object[]{1,2,3}, impl.getMap().get("1").toArray());
    }

    @Test
    public void testWildcardMapProvider() {
        Injector injector = DIBootstrap.createInjector(new MapProviderModule(), b -> {
            b.bind(Service.class).to(Service_Impl2.class);
        });

        Service service = injector.getInstance(Service.class);

        assertThat(service, instanceOf(Service_Impl2.class));

        Service_Impl2 impl = (Service_Impl2)service;
        assertArrayEquals(new Object[]{1,2,3}, impl.getMap().get("1").toArray());
    }

    @Test
    public void testPutKey() {
        Injector injector = DIBootstrap.createInjector(b -> {
            b.bind(Key.get(String.class, "1")).toInstance("str1");
            b.bind(Key.get(String.class, "2")).toInstance("str2");
            b.bindMap(Integer.class, String.class)
                    .put(1, Key.get(String.class, "1"))
                    .put(2, Key.get(String.class, "2"));
        });

        Map<Integer,String> map = injector.getInstance(Key.getMapOf(Integer.class, String.class));
        assertEquals(2, map.size());
        assertThat(map.values(), hasItems("str1","str2"));
    }

    private void assertMapContent(Injector injector) {
        Service service = injector.getInstance(Service.class);

        assertThat(service, instanceOf(Service_Impl1.class));

        Service_Impl1 impl = (Service_Impl1)service;

        assertEquals("1", impl.getMapByInteger().get(1));
        assertEquals("2", impl.getMapByInteger().get(2));
        assertEquals("3", impl.getMapByString().get("3"));
        assertEquals("4", impl.getMapByString().get("4"));
    }

    private static final Module serviceModule1 = b -> b.bind(Service.class).to(Service_Impl1.class);

    interface Service {}

    private static class Service_Impl1 implements Service {
        @Inject
        private Map<String, String> mapByString;
        @Inject
        private Map<Integer, String> mapByInteger;

        Map<String, String> getMapByString() {
            return mapByString;
        }
        Map<Integer, String> getMapByInteger() {
            return mapByInteger;
        }
    }

    private static class Service_Impl2 implements Service {
        @Inject
        private Map<String, List<? extends Number>> map;
        Map<String, List<? extends Number>> getMap() {
            return map;
        }
    }

    public static class MapProviderModule extends BaseModule {

        @Provides
        public Map<Integer, String> createIntegerMap() {
            Map<Integer, String> integerMap = new HashMap<>();
            integerMap.put(1, "1");
            integerMap.put(2, "2");
            return integerMap;
        }

        @Provides
        public Map<String, String> createStringMap() {
            Map<String, String> stringMap = new HashMap<>();
            stringMap.put("3", "3");
            stringMap.put("4", "4");
            return stringMap;
        }

        @Provides
        public Map<String, List<? extends Number>> createListMap() {
            Map<String, List<? extends Number>> stringMap = new HashMap<>();
            stringMap.put("1", Arrays.asList(1, 2, 3));
            return stringMap;
        }
    }
}
