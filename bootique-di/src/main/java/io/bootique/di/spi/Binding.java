package io.bootique.di.spi;

import io.bootique.di.Scope;

import javax.inject.Provider;
import java.util.List;

/**
 * A binding encapsulates DI provider scoping settings and allows to change them as many
 * times as needed.
 */
class Binding<T> {

    private Provider<T> original;
    private Provider<T> decorated;
    private Provider<T> scoped;
    private Scope scope;

    Binding(Provider<T> provider, Scope initialScope) {
        this.original = provider;
        this.decorated = provider;

        changeScope(initialScope);
    }

    void changeScope(Scope scope) {
        if (scope == null) {
            scope = NoScope.INSTANCE;
        }

        // TODO: what happens to the old scoped value? Seems like this leaks
        // scope event listeners and may cause unexpected events...

        this.scoped = scope.scope(original);
        this.scope = scope;
    }

    void decorate(Decoration<T> decoration) {

        List<DecoratorProvider<T>> decorators = decoration.decorators();
        if (decorators.isEmpty()) {
            return;
        }

        Provider<T> provider = this.original;
        for (DecoratorProvider<T> decoratorProvider : decorators) {
            provider = decoratorProvider.get(provider);
        }

        this.decorated = provider;

        // TODO: what happens to the old scoped value? Seems like this leaks
        // scope event listeners and may cause unexpected events...

        this.scoped = scope.scope(decorated);
    }

    Provider<T> getOriginal() {
        return original;
    }

    Provider<T> getScoped() {
        return scoped;
    }
}
