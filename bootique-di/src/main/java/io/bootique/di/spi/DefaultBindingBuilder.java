package io.bootique.di.spi;

import io.bootique.di.BindingBuilder;
import io.bootique.di.DIRuntimeException;
import io.bootique.di.Key;
import io.bootique.di.Scope;

import javax.inject.Provider;

class DefaultBindingBuilder<T> implements BindingBuilder<T> {

    protected DefaultInjector injector;
    protected Key<T> bindingKey;

    DefaultBindingBuilder(Key<T> bindingKey, DefaultInjector injector) {
        this.injector = injector;
        this.bindingKey = bindingKey;
    }

    @Override
    public BindingBuilder<T> to(Class<? extends T> implementation) throws DIRuntimeException {
        Provider<T> provider0 = new ConstructorInjectingProvider<>(implementation, injector);
        Provider<T> provider1 = new FieldInjectingProvider<>(provider0, injector);

        injector.putBinding(bindingKey, provider1);
        return this;
    }

    @Override
    public BindingBuilder<T> to(Key<? extends T> key) throws DIRuntimeException {
        return toProviderInstance(() -> injector.getProvider(key).get());
    }

    @Override
    public BindingBuilder<T> toInstance(T instance) throws DIRuntimeException {
        Provider<T> provider0 = new InstanceProvider<>(instance);
        Provider<T> provider1 = new FieldInjectingProvider<>(provider0, injector);

        injector.putBinding(bindingKey, provider1);
        return this;
    }

    @Override
    public BindingBuilder<T> toProvider(Class<? extends Provider<? extends T>> providerType) {
        Provider<Provider<? extends T>> provider0 = new ConstructorInjectingProvider<>(providerType, injector);
        Provider<Provider<? extends T>> provider1 = new FieldInjectingProvider<>(provider0, injector);

        Provider<T> provider2 = new CustomProvidersProvider<>(provider1);
        Provider<T> provider3 = new FieldInjectingProvider<>(provider2, injector);

        injector.putBinding(bindingKey, provider3);
        return this;
    }

    @Override
    public BindingBuilder<T> toProviderInstance(Provider<? extends T> provider) {
        Provider<Provider<? extends T>> provider0 = new InstanceProvider<>(provider);
        Provider<Provider<? extends T>> provider1 = new FieldInjectingProvider<>(provider0, injector);

        Provider<T> provider2 = new CustomProvidersProvider<>(provider1);
        Provider<T> provider3 = new FieldInjectingProvider<>(provider2, injector);

        injector.putBinding(bindingKey, provider3);
        return this;
    }

    @Override
    public void in(Scope scope) {
        injector.changeBindingScope(bindingKey, scope);
    }

    @Override
    public void withoutScope() {
        in(injector.getNoScope());
    }

    @Override
    public void inSingletonScope() {
        in(injector.getSingletonScope());
    }
}
