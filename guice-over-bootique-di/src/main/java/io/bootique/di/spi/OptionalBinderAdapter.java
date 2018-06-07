package io.bootique.di.spi;

import com.google.inject.Key;
import com.google.inject.binder.LinkedBindingBuilder;

public class OptionalBinderAdapter<T> {

    private final BinderAdapter guiceBinderAdapter;
    private final OptionalBindingBuilderAdapter<T> adapter;
    private final Key<T> key;

    public OptionalBinderAdapter(com.google.inject.Binder guiceBinder, Key<T> key) {
        this.key = key;
        this.guiceBinderAdapter = (BinderAdapter) guiceBinder;
        this.adapter = createBindingBuilder();
    }

    public LinkedBindingBuilder<T> setDefault() {
        return adapter;
    }

    public LinkedBindingBuilder<T> setBinding() {
        return adapter;
    }

    private OptionalBindingBuilderAdapter<T> createBindingBuilder() {
        OptionalBindingBuilderAdapter<T> adapter = new OptionalBindingBuilderAdapter<>(
                guiceBinderAdapter, ConversionUtils.toBootiqueKey(key));
        guiceBinderAdapter.getInjectorAdapter().registerBindingBuilder(adapter);
        return adapter;
    }
}
