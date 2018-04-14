/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.inject.util;

import com.google.inject.Binder;
import com.google.inject.Module;
import io.bootique.di.spi.BinderAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Static utility methods for creating and working with instances of {@link Module}.
 *
 * @author jessewilson@google.com (Jesse Wilson)
 * @since 2.0
 */
public final class Modules {
    private Modules() {
    }

    /**
     * Returns a builder that creates a module that overlays override modules over the given modules.
     * If a key is bound in both sets of modules, only the binding from the override modules is kept.
     * This can be used to replace the bindings of a production module with test bindings:
     *
     * <pre>
     * Module functionalTestModule
     *     = Modules.override(new ProductionModule()).with(new TestModule());
     * </pre>
     *
     * <p>Prefer to write smaller modules that can be reused and tested without overrides.
     *
     * @param modules the modules whose bindings are open to be overridden
     */
    public static OverriddenModuleBuilder override(Module... modules) {
        return new RealOverriddenModuleBuilder(Arrays.asList(modules));
    }

    /**
     * Returns a builder that creates a module that overlays override modules over the given modules.
     * If a key is bound in both sets of modules, only the binding from the override modules is kept.
     * This can be used to replace the bindings of a
     * production module with test bindings:
     *
     * <pre>
     * Module functionalTestModule
     *     = Modules.override(getProductionModules()).with(getTestModules());
     * </pre>
     *
     * <p>Prefer to write smaller modules that can be reused and tested without overrides.
     *
     * @param modules the modules whose bindings are open to be overridden
     */
    public static OverriddenModuleBuilder override(Iterable<? extends Module> modules) {
        return new RealOverriddenModuleBuilder(modules);
    }

    /**
     * See the EDSL example at {@link Modules#override(Module[]) override()}.
     */
    public interface OverriddenModuleBuilder {

        /**
         * See the EDSL example at {@link Modules#override(Module[]) override()}.
         */
        Module with(Module... overrides);

        /**
         * See the EDSL example at {@link Modules#override(Module[]) override()}.
         */
        Module with(Iterable<? extends Module> overrides);
    }

    private static final class RealOverriddenModuleBuilder implements OverriddenModuleBuilder {
        private final Set<Module> baseModules;

        private RealOverriddenModuleBuilder(Iterable<? extends Module> baseModules) {
            this.baseModules = new HashSet<>();
            for (Module m : baseModules) {
                this.baseModules.add(m);
            }
        }

        @Override
        public Module with(Module... overrides) {
            return with(Arrays.asList(overrides));
        }

        @Override
        public Module with(Iterable<? extends Module> overrides) {
            return new OverrideModule(baseModules, overrides);
        }
    }

    static class OverrideModule implements Module {
        private final List<Module> baseModules;

        OverrideModule(Set<Module> baseModules, Iterable<? extends Module> overrides) {
            this.baseModules = new ArrayList<>(baseModules);
            for (Module m : overrides) {
                this.baseModules.add(m);
            }
        }

        @Override
        public void configure(Binder binder) {
            BinderAdapter adapter = (BinderAdapter) binder;
            baseModules.forEach(m -> adapter.getInjectorAdapter().installModule(m));
        }
    }


}
