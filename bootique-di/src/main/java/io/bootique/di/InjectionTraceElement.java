package io.bootique.di;


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
    private String message;

    public InjectionTraceElement(Key<?> bindingKey) {
        this.bindingKey = bindingKey;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Key<?> getBindingKey() {
        return bindingKey;
    }

    public String toString() {
        if (message == null) {
            return bindingKey.toString();
        }

        return bindingKey + " -> " + message;
    }
}
