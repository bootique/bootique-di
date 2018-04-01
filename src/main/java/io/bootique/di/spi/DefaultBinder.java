package io.bootique.di.spi;

import io.bootique.di.Binder;
import io.bootique.di.BindingBuilder;
import io.bootique.di.DecoratorBuilder;
import io.bootique.di.Key;
import io.bootique.di.ListBuilder;
import io.bootique.di.MapBuilder;

class DefaultBinder implements Binder {

    private DefaultInjector injector;

    DefaultBinder(DefaultInjector injector) {
        this.injector = injector;
    }

    @Override
    public <T> BindingBuilder<T> bind(Class<T> interfaceType) {
        return new DefaultBindingBuilder<>(Key.get(interfaceType), injector);
    }

    @Override
    public <T> BindingBuilder<T> bind(Key<T> key) {
        return new DefaultBindingBuilder<>(key, injector);
    }

    @Override
    public <T> ListBuilder<T> bindList(Class<T> valueType) {
        return bindList(valueType, null);
    }

    @Override
    public <T> ListBuilder<T> bindList(Class<T> valueType, String bindingName) {
        return new DefaultListBuilder<>(Key.getListOf(valueType, bindingName), injector);
    }

    @Override
    public <T> MapBuilder<T> bindMap(Class<T> valueType) {
        return bindMap(valueType, null);
    }

    @Override
    public <T> MapBuilder<T> bindMap(Class<T> valueType, String bindingName) {
        return new DefaultMapBuilder<>(Key.getMapOf(String.class, valueType, bindingName), injector);
    }

    @Override
    public <T> DecoratorBuilder<T> decorate(Class<T> interfaceType) {
        return new DefaultDecoratorBuilder<>(Key.get(interfaceType), injector);
    }

    @Override
    public <T> DecoratorBuilder<T> decorate(Key<T> key) {
        return new DefaultDecoratorBuilder<>(key, injector);
    }
}
