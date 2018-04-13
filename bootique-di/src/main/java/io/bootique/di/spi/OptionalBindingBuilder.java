package io.bootique.di.spi;

import io.bootique.di.Key;

public class OptionalBindingBuilder<T> extends DefaultBindingBuilder<T> {

    OptionalBindingBuilder(Key<T> bindingKey, DefaultInjector injector) {
        super(bindingKey, injector);

    }
}
