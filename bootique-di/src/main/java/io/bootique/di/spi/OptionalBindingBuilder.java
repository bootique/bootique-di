package io.bootique.di.spi;

import java.util.Optional;
import javax.inject.Provider;

import io.bootique.di.Key;

class OptionalBindingBuilder<T> extends DefaultBindingBuilder<T> {

    static final Provider<?> NULL_PROVIDER = () -> null;

    OptionalBindingBuilder(Key<T> bindingKey, DefaultInjector injector) {
        super(bindingKey, injector);
    }

    @Override
    protected void initBinding() {
        Binding<T> binding = injector.getBinding(bindingKey);
        // do not override existing binding with optional one
        if(binding == null) {
            injector.putOptionalBinding(bindingKey, nullProvider());
        }
        // add binding to Optional<T> type
        injector.putBinding(Key.getOptionalOf(bindingKey), () -> {
            T value = injector.getProvider(bindingKey).get();
            return value == null ? Optional.empty() : Optional.of(value);
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> Provider<T> nullProvider() {
        return (Provider<T>)NULL_PROVIDER;
    }
}
