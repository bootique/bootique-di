
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

package io.bootique.di.spi;

import io.bootique.di.Key;
import io.bootique.di.DIModule;
import io.bootique.di.TypeLiteral;
import io.bootique.di.mock.MockImplementation1;
import io.bootique.di.mock.MockImplementation1Alt;
import io.bootique.di.mock.MockImplementation1Alt2;
import io.bootique.di.mock.MockImplementation1_MapConfiguration;
import io.bootique.di.mock.MockImplementation1_MapWithWildcards;
import io.bootique.di.mock.MockImplementation1_WithInjector;
import io.bootique.di.mock.MockImplementation2;
import io.bootique.di.mock.MockImplementation2Sub1;
import io.bootique.di.mock.MockImplementation2_ConstructorProvider;
import io.bootique.di.mock.MockImplementation2_Named;
import io.bootique.di.mock.MockImplementation3;
import io.bootique.di.mock.MockImplementation4;
import io.bootique.di.mock.MockImplementation4Alt;
import io.bootique.di.mock.MockImplementation4Alt2;
import io.bootique.di.mock.MockImplementation5;
import io.bootique.di.mock.MockInterface1;
import io.bootique.di.mock.MockInterface2;
import io.bootique.di.mock.MockInterface3;
import io.bootique.di.mock.MockInterface4;
import io.bootique.di.mock.MockInterface5;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class DefaultInjectorInjectionTest {

    @Test
    public void testFieldInjection() {

        DIModule module = binder -> {
            binder.bind(MockInterface1.class).to(MockImplementation1.class);
            binder.bind(MockInterface2.class).to(MockImplementation2.class);
        };

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface2 service = injector.getInstance(MockInterface2.class);
        assertNotNull(service);
        assertEquals("altered_MyName", service.getAlteredName());
    }

    @Test
    public void testFieldInjection_Named() {

        DIModule module = binder -> {
            binder.bind(MockInterface1.class).to(MockImplementation1.class);
            binder.bind(Key.get(MockInterface1.class, "one")).to(MockImplementation1Alt.class);
            binder.bind(Key.get(MockInterface1.class, "two")).to(MockImplementation1Alt2.class);
            binder.bind(MockInterface2.class).to(MockImplementation2_Named.class);
        };

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface2 service = injector.getInstance(MockInterface2.class);
        assertNotNull(service);
        assertEquals("altered_alt", service.getAlteredName());
    }

    @Test
    public void testFieldInjectionSuperclass() {

        DIModule module = binder -> {
            binder.bind(MockInterface1.class).to(MockImplementation1.class);
            binder.bind(MockInterface2.class).to(MockImplementation2Sub1.class);
            binder.bind(MockInterface3.class).to(MockImplementation3.class);
        };

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface2 service = injector.getInstance(MockInterface2.class);
        assertNotNull(service);
        assertEquals("altered_MyName:XName", service.getAlteredName());
    }

    @Test
    public void testConstructorInjection() {

        DIModule module = binder -> {
            binder.bind(MockInterface1.class).to(MockImplementation1.class);
            binder.bind(MockInterface4.class).to(MockImplementation4.class);
        };

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface4 service = injector.getInstance(MockInterface4.class);
        assertNotNull(service);
        assertEquals("constructor_MyName", service.getName());
    }

    @Test
    public void testConstructorInjection_Named() {

        DIModule module = binder -> {
            binder.bind(MockInterface1.class).to(MockImplementation1.class);
            binder.bind(Key.get(MockInterface1.class, "one")).to(MockImplementation1Alt.class);
            binder.bind(Key.get(MockInterface1.class, "two")).to(MockImplementation1Alt2.class);
            binder.bind(MockInterface4.class).to(MockImplementation4Alt.class);
        };

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface4 service = injector.getInstance(MockInterface4.class);
        assertNotNull(service);
        assertEquals("constructor_alt2", service.getName());
    }

    @Test
    public void testConstructorInjection_Named_Mixed() {

        DIModule module = binder -> {
            binder.bind(MockInterface1.class).to(MockImplementation1.class);
            binder.bind(Key.get(MockInterface1.class, "one")).to(MockImplementation1Alt.class);
            binder.bind(Key.get(MockInterface1.class, "two")).to(MockImplementation1Alt2.class);
            binder.bind(MockInterface3.class).to(MockImplementation3.class);
            binder.bind(MockInterface4.class).to(MockImplementation4Alt2.class);
        };

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface4 service = injector.getInstance(MockInterface4.class);
        assertNotNull(service);
        assertEquals("constructor_alt2_XName", service.getName());
    }

    @Test
    public void testProviderInjection_Constructor() {

        DIModule module = binder -> {
            binder.bind(MockInterface1.class).to(MockImplementation1.class);
            binder.bind(MockInterface2.class).to(MockImplementation2_ConstructorProvider.class);
        };

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface2 service = injector.getInstance(MockInterface2.class);
        assertEquals("altered_MyName", service.getAlteredName());
    }

    @Test
    public void testMapInjection_Empty() {
        DIModule module = binder -> {
            binder.bind(MockInterface1.class).to(MockImplementation1_MapConfiguration.class);

            // empty map must be still bound
            binder.bindMap(String.class, Object.class, "xyz");
        };

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface1 service = injector.getInstance(MockInterface1.class);
        assertNotNull(service);
        assertEquals("", service.getName());
    }

    @Test
    public void testMapInjection() {
        DIModule module = binder -> {
            binder.bind(MockInterface1.class).to(MockImplementation1_MapConfiguration.class);
            binder.bindMap(String.class, Object.class,"xyz")
                    .put("x", "xvalue").put("y", "yvalue").put("x", "xvalue1");
        };

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface1 service = injector.getInstance(MockInterface1.class);
        assertNotNull(service);
        assertEquals(";x=xvalue1;y=yvalue", service.getName());
    }

    @Test
    public void mapWithWildcardInjection() {
        DIModule module = binder -> {
            binder.bind(MockInterface1.class).to(MockImplementation1_MapWithWildcards.class);
            binder.bindMap(new TypeLiteral<String>(){}, new TypeLiteral<Class<?>>(){})
                    .put("x", String.class).put("y", Integer.class).put("z", Object.class);
        };
        DefaultInjector injector = new DefaultInjector(module);

        // This is example of how to deal with wildcards:
        Map<String, Class<?>> map = injector.getInstance(Key.getMapOf(new TypeLiteral<String>(){}, new TypeLiteral<Class<?>>(){}));

        assertNotNull(map);
        assertEquals(3, map.size());
        assertEquals(String.class, map.get("x"));

        MockInterface1 service = injector.getInstance(MockInterface1.class);
        assertNotNull(service);
        assertEquals("map:3", service.getName());
    }

    @Test
    public void testMapInjection_Resumed() {
        DIModule module = binder -> {
            binder.bind(MockInterface1.class).to(MockImplementation1_MapConfiguration.class);
            // bind 1
            binder.bindMap(String.class, Object.class,"xyz").put("x", "xvalue").put("y", "yvalue");
            // second binding attempt to the same map...
            binder.bindMap(String.class, Object.class,"xyz").put("z", "zvalue").put("x", "xvalue1");
        };

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface1 service = injector.getInstance(MockInterface1.class);
        assertNotNull(service);
        assertEquals(";x=xvalue1;y=yvalue;z=zvalue", service.getName());
    }

    @Test
    public void testMapInjection_OverrideExplicitlyBoundType() {
        DIModule m1 = binder -> {
            binder.bind(MockInterface5.class).to(MockImplementation5.class);
            binder.bind(MockInterface1.class).to(MockImplementation1_MapConfiguration.class);

            binder.bindMap(String.class, Object.class, "xyz").put("a", MockInterface5.class);
        };

        DIModule m2 = binder -> binder.bind(MockInterface5.class).toInstance(new MockInterface5() {

            @Override
            public String toString() {
                return "abc";
            }
        });

        MockInterface1 service = new DefaultInjector(m1, m2).getInstance(MockInterface1.class);
        assertEquals("Map element was not overridden in submodule", ";a=abc", service.getName());
    }

    @Test
    public void testMapInjection_OverrideImplicitlyBoundType() {
        DIModule m1 = binder -> {
            binder.bind(MockInterface1.class).to(MockImplementation1_MapConfiguration.class);
            binder.bindMap(String.class, Object.class, "xyz").put("a", MockImplementation5.class);
        };

        DIModule m2 = binder -> binder.bind(MockImplementation5.class).toInstance(new MockImplementation5() {

            @Override
            public String toString() {
                return "abc";
            }
        });

        MockInterface1 service = new DefaultInjector(m1, m2).getInstance(MockInterface1.class);
        assertEquals("Map element was not overridden in submodule", ";a=abc", service.getName());
    }


    @Test
    public void testInjectorInjection() {
        DIModule module = binder -> binder.bind(MockInterface1.class).to(
                MockImplementation1_WithInjector.class);

        DefaultInjector injector = new DefaultInjector(module);

        MockInterface1 service = injector.getInstance(MockInterface1.class);
        assertNotNull(service);
        assertEquals("injector_not_null", service.getName());
    }

}
