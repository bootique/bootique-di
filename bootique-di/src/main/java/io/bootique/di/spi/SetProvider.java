package io.bootique.di.spi;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.inject.Provider;

import io.bootique.di.Key;

class SetProvider<T> implements Provider<Set<T>> {

    private final DefaultInjector injector;
    private final Collection<Provider<? extends T>> providers;
    private final Key<Set<T>> bindingKey;

    SetProvider(DefaultInjector injector, Key<Set<T>> bindingKey) {
        this.injector = injector;
        this.providers = new ConcurrentLinkedQueue<>();
        this.bindingKey = bindingKey;
    }

    @Override
    public Set<T> get() {
        Set<T> set = new HashSet<>(providers.size());
        int i = 0;
        for (Provider<? extends T> provider : providers) {
            int idx = i++;
            injector.trace(() -> "Resolving set element " + idx);
            T value = provider.get();
            if (!set.add(value)) {
                injector.throwException("Found duplicated value '%s' in set %s.", value, bindingKey);
            }
        }

        return set;
    }

    void add(Provider<? extends T> provider) {
        providers.add(provider);
    }

}
