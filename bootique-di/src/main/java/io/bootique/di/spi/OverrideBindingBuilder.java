package io.bootique.di.spi;

import javax.inject.Provider;

import io.bootique.di.Key;

class OverrideBindingBuilder<T> extends DefaultBindingBuilder<T> {

    OverrideBindingBuilder(Key<T> key, DefaultInjector injector) {
        super(key, injector);
    }

    @Override
    protected void initBinding() {
        Binding<T> binding = injector.getBinding(bindingKey);
        if(binding == null) {
            injector.throwException("No binding to override for key %s", bindingKey);
        }
    }

    @Override
    protected void addBinding(Provider<T> provider) {
        injector.overrideBinding(bindingKey, provider);
    }
}
