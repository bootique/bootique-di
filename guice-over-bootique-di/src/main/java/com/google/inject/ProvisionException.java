package com.google.inject;

import java.util.Collection;
import java.util.Collections;

import com.google.inject.spi.Message;
import io.bootique.di.DIRuntimeException;

public final class ProvisionException extends DIRuntimeException {
    public ProvisionException() {
    }

    public ProvisionException(String messageFormat, Object... messageArgs) {
        super(messageFormat, messageArgs);
    }

    public ProvisionException(Throwable cause) {
        super(cause);
    }

    public ProvisionException(String messageFormat, Throwable cause, Object... messageArgs) {
        super(messageFormat, cause, messageArgs);
    }

    /**
     * Returns messages for the errors that caused this exception.
     */
    public Collection<Message> getErrorMessages() {
        return Collections.singleton(new Message(getMessage(), getCause()));
    }
}
