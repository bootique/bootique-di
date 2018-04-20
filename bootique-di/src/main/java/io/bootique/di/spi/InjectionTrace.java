package io.bootique.di.spi;

import java.util.LinkedList;

import io.bootique.di.InjectionTraceElement;
import io.bootique.di.Key;

/**
 * Optional detailed trace of injection.
 * Can be used in dev environment to create more user-friendly messages in case of injection errors.
 */
class InjectionTrace {

    private ThreadLocal<LinkedList<InjectionTraceElement>> stack;

    InjectionTrace() {
        this.stack = new ThreadLocal<>();
    }

    void push(Key<?> key) {
        getStack().push(new InjectionTraceElement(key));
    }

    void updateMessage(String message) {
        InjectionTraceElement element = getStack().peekFirst();
        if(element != null) {
            element.setMessage(message);
        }
    }

    InjectionTraceElement pop() {
        return getStack().pollFirst();
    }

    int size() {
        return getStack().size();
    }

    private LinkedList<InjectionTraceElement> getStack() {
        LinkedList<InjectionTraceElement> localStack = stack.get();
        if(localStack == null) {
            localStack = new LinkedList<>();
            stack.set(localStack);
        }
        return localStack;
    }

}
