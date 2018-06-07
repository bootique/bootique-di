package io.bootique.di;

/**
 * A runtime exception thrown on DI misconfiguration.
 */
public class DIRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 396131653561690312L;

    private InjectionTraceElement[] injectionTrace = {};

    /**
     * Creates new <code>DIRuntimeException</code> without detail message.
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
        // we suppressing stack trace in case this exception has cause, as it is likely irrelevant
        super(String.format(messageFormat, messageArgs), cause, true, cause == null);
    }

    public void setInjectionTrace(InjectionTraceElement[] injectionTrace) {
        this.injectionTrace = injectionTrace;
    }

    public InjectionTraceElement[] getInjectionTrace() {
        return injectionTrace;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(getOriginalMessage());
        if(injectionTrace.length > 0) {
            sb.append("\n\n Injection trace: \n");
            for(int i=0; i<injectionTrace.length; i++) {
                sb.append("\n [").append(i).append(']').append(" resolving key ").append(injectionTrace[i].getBindingKey());
                sb.append("\n    -> ").append(injectionTrace[i].getMessage()).append('\n');
            }
        }
        return sb.toString();
    }

    public String getOriginalMessage() {
        return super.getMessage();
    }
}
