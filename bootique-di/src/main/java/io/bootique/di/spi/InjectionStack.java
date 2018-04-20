package io.bootique.di.spi;

import io.bootique.di.Key;

import java.util.LinkedList;
import java.util.List;

/**
 * A helper object that tracks the injection stack to prevent circular dependencies.
 */
class InjectionStack {

    private final DefaultInjector injector;
    private final ThreadLocal<LinkedList<Key<?>>> stack;

    InjectionStack(DefaultInjector injector) {
        this.injector = injector;
        this.stack = new ThreadLocal<>();
    }

    void reset() {
        List<Key<?>> localStack = stack.get();
        if (localStack != null) {
            localStack.clear();
        }
    }

    void push(Key<?> bindingKey) {
        LinkedList<Key<?>> localStack = stack.get();
        if (localStack == null) {
            localStack = new LinkedList<>();
            stack.set(localStack);
        }

        if (localStack.contains(bindingKey)) {
            injector.throwException(
                    "Circular dependency detected when binding a key %s. Nested keys: %s"
                            + ". To resolve it, you should inject a Provider instead of an object.",
                    bindingKey,
                    localStack);
        }

        localStack.add(bindingKey);
    }

    void pop() {
        LinkedList<Key<?>> localStack = stack.get();
        if (localStack != null) {
            localStack.removeLast();
        } else {
            throw new IndexOutOfBoundsException("0");
        }
    }

    @Override
    public String toString() {
        List<Key<?>> localStack = stack.get();
        if (localStack != null) {
            return String.valueOf(localStack);
        } else {
            return "[]";
        }
    }
}
