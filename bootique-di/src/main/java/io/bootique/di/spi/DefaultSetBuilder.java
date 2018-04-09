package io.bootique.di.spi;

import java.util.Collection;
import java.util.Set;

import javax.inject.Provider;

import io.bootique.di.Key;
import io.bootique.di.SetBuilder;

class DefaultSetBuilder<T> extends DICollectionBuilder<Set<T>, T> implements SetBuilder<T> {

    DefaultSetBuilder(Key<Set<T>> bindingKey, DefaultInjector injector) {
        super(bindingKey, injector);
        findOrCreateSetProvider();
    }

    @Override
    public SetBuilder<T> add(Class<? extends T> interfaceType) {
        Provider<? extends T> provider = createTypeProvider(interfaceType);
        findOrCreateSetProvider().add(provider);
        return this;
    }

    @Override
    public SetBuilder<T> add(T value) {
        findOrCreateSetProvider().add(createInstanceProvider(value));
        return this;
    }

    @Override
    public SetBuilder<T> addAll(Collection<T> values) {
        SetProvider<T> provider = findOrCreateSetProvider();
        for (T object : values) {
            provider.add(createInstanceProvider(object));
        }
        return this;
    }

    private SetProvider<T> findOrCreateSetProvider() {

        SetProvider<T> provider;
        Binding<Set<T>> binding = injector.getBinding(bindingKey);
        if (binding == null) {
            provider = new SetProvider<>(bindingKey);
            injector.putBinding(bindingKey, provider);
        } else {
            provider = (SetProvider<T>) binding.getOriginal();
        }

        return provider;
    }
}
