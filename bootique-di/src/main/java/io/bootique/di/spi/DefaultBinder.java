package io.bootique.di.spi;

import java.util.Map;

import io.bootique.di.Binder;
import io.bootique.di.BindingBuilder;
import io.bootique.di.DecoratorBuilder;
import io.bootique.di.Key;
import io.bootique.di.ListBuilder;
import io.bootique.di.MapBuilder;
import io.bootique.di.SetBuilder;
import io.bootique.di.TypeLiteral;

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
    public <T> SetBuilder<T> bindSet(Class<T> valueType) {
        return bindSet(valueType, null);
    }

    @Override
    public <T> SetBuilder<T> bindSet(TypeLiteral<T> valueType, String bindingName) {
        return new DefaultSetBuilder<>(Key.get(TypeLiteral.setOf(valueType), bindingName), injector);
    }

    @Override
    public <T> SetBuilder<T> bindSet(TypeLiteral<T> valueType) {
        return bindSet(valueType, null);
    }

    @Override
    public <T> SetBuilder<T> bindSet(Class<T> valueType, String bindingName) {
        return new DefaultSetBuilder<>(Key.getSetOf(valueType, bindingName), injector);
    }

    @Override
    public <K, V> MapBuilder<K, V> bindMap(Class<K> keyType, Class<V> valueType) {
        return bindMap(Key.getMapOf(keyType, valueType));
    }

    @Override
    public <K, V> MapBuilder<K, V> bindMap(Class<K> keyType, Class<V> valueType, String bindingName) {
        return bindMap(Key.getMapOf(keyType, valueType, bindingName));
    }

    @Override
    public <K, V> MapBuilder<K, V> bindMap(TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return bindMap(Key.getMapOf(keyType, valueType));
    }

    @Override
    public <K, V> MapBuilder<K, V> bindMap(TypeLiteral<K> keyType, TypeLiteral<V> valueType, String bindingName) {
        return bindMap(Key.getMapOf(keyType, valueType, bindingName));
    }

    @Override
    public <T> DecoratorBuilder<T> decorate(Class<T> interfaceType) {
        return new DefaultDecoratorBuilder<>(Key.get(interfaceType), injector);
    }

    @Override
    public <T> DecoratorBuilder<T> decorate(Key<T> key) {
        return new DefaultDecoratorBuilder<>(key, injector);
    }

    private <K, V> MapBuilder<K, V> bindMap(Key<Map<K, V>> mapKey) {
        return new DefaultMapBuilder<>(mapKey, injector);
    }
}
