
package io.bootique.di.spi;

import java.util.LinkedList;
import java.util.List;

import io.bootique.di.DIRuntimeException;
import io.bootique.di.Key;

/**
 * A helper object that tracks the injection stack to prevent circular dependencies.
 * 
 * @since 3.1
 */
class InjectionStack {

    private ThreadLocal<LinkedList<Key<?>>> stack;

    InjectionStack() {
        this.stack = new ThreadLocal<LinkedList<Key<?>>>();
    }

    void reset() {
        List<Key<?>> localStack = stack.get();
        if (localStack != null) {
            localStack.clear();
        }
    }

    void push(Key<?> bindingKey) throws DIRuntimeException {
        LinkedList<Key<?>> localStack = stack.get();
        if (localStack == null) {
            localStack = new LinkedList<Key<?>>();
            stack.set(localStack);
        }

        if (localStack.contains(bindingKey)) {
            throw new DIRuntimeException(
                    "Circular dependency detected when binding a key \"%s\". Nested keys: %s"
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
        }
        else {
            throw new IndexOutOfBoundsException("0");
        }
    }

    @Override
    public String toString() {
        List<Key<?>> localStack = stack.get();
        if (localStack != null) {
            return String.valueOf(localStack);
        }
        else {
            return "[]";
        }
    }
}