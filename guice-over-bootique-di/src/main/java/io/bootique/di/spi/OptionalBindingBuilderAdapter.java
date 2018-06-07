package io.bootique.di.spi;

import io.bootique.di.BindingBuilder;
import io.bootique.di.Key;

public class OptionalBindingBuilderAdapter<T> extends BindingBuilderAdapter<T> {

    OptionalBindingBuilderAdapter(BinderAdapter binderAdapter, Key<T> bootiqueKey) {
        super(binderAdapter, bootiqueKey);
    }

    BindingBuilder<T> getBinding() {
        if(bindingBuilder == null) {
            bindingBuilder = binderAdapter.getBootiqueBinder().bindOptional(bootiqueKey);
        }
        return bindingBuilder;
    }
}
