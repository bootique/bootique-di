package io.bootique.di.spi;

import javax.inject.Provider;

import io.bootique.di.Key;

class OptionalBindingBuilder<T> extends DefaultBindingBuilder<T> {

    private static final Provider<?> NULL_PROVIDER = () -> null;

    OptionalBindingBuilder(Key<T> bindingKey, DefaultInjector injector) {
        super(bindingKey, injector);
    }

    @Override
    protected void initBinding() {
        injector.putOptionalBinding(bindingKey, nullProvider());
    }

    @SuppressWarnings("unchecked")
    private static <T> Provider<T> nullProvider() {
        return (Provider<T>)NULL_PROVIDER;
    }
}
