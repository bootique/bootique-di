package io.bootique.di.spi;

import io.bootique.di.Module;

import java.util.Collection;
import java.util.Collections;

class ProvidesHandler {

    static Collection<KeyBindingPair<?>> bindingsFromProviderMethods(Module module) {
        return Collections.emptyList();
    }
}
