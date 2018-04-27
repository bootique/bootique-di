package io.bootique.di;

import java.util.function.Supplier;

/**
 * Injection stack trace element.
 */
public class InjectionTraceElement {

    /**
     * Binding key of this trace element
     */
    private final Key<?> bindingKey;

    /**
     * Last human readable message for binding key.
     */
    private Supplier<String> message;

    public InjectionTraceElement(Key<?> bindingKey) {
        this.bindingKey = bindingKey;
    }

    public void setMessage(Supplier<String> messageSupplier) {
        this.message = messageSupplier;
    }

    public String getMessage() {
        return message.get();
    }

    public Key<?> getBindingKey() {
        return bindingKey;
    }

    public String toString() {
        if (message == null) {
            return bindingKey.toString();
        }

        return bindingKey + " -> " + message.get();
    }
}
