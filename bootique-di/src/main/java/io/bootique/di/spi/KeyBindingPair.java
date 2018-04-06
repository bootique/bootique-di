package io.bootique.di.spi;

import io.bootique.di.Key;

class KeyBindingPair<T> {

    private Key<T> key;
    private Binding<T> binding;

    KeyBindingPair(Key<T> key, Binding<T> binding) {
        this.key = key;
        this.binding = binding;
    }

    public void bind(DefaultInjector injector) {
        injector.putBinding(key, binding);
    }
}
