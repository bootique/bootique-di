package io.bootique.di;

/**
 * A runtime exception thrown on DI misconfiguration.
 */
public class DIRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 396131653561690312L;

    /**
     * Creates new <code>ConfigurationException</code> without detail message.
     */
    public DIRuntimeException() {
    }

    /**
     * Constructs an exception with the specified message with an optional list
     * of message formatting arguments. Message formatting rules follow
     * "String.format(..)" conventions.
     */
    public DIRuntimeException(String messageFormat, Object... messageArgs) {
        super(String.format(messageFormat, messageArgs));
    }

    /**
     * Constructs an exception wrapping another exception thrown elsewhere.
     */
    public DIRuntimeException(Throwable cause) {
        super(cause);
    }

    public DIRuntimeException(String messageFormat, Throwable cause, Object... messageArgs) {
        super(String.format(messageFormat, messageArgs), cause);
    }
}
