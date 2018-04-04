/*
 * Copyright (C) 2006 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.inject.spi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * An error message and the context in which it occured. Messages are usually created internally by
 * Guice and its extensions.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public final class Message implements Serializable {
    private final String message;
    private final Throwable cause;
    private final List<Object> sources;

    /**
     * @since 2.0
     */
    private Message(List<Object> sources, String message, Throwable cause) {
        this.sources = new ArrayList<>(sources);
        this.message = Objects.requireNonNull(message, "message");
        this.cause = cause;
    }

    /**
     * @since 4.0
     */
    public Message(String message, Throwable cause) {
        this(Collections.emptyList(), message, cause);
    }

    public Message(String message) {
        this(Collections.emptyList(), message, null);
    }

    /**
     * @since 2.0
     */
    List<Object> getSources() {
        return sources;
    }

    /**
     * Gets the error message text.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the throwable that caused this message, or {@code null} if this message was not caused
     * by a throwable.
     *
     * @since 2.0
     */
    public Throwable getCause() {
        return cause;
    }

    @Override
    public String toString() {
        return message;
    }

    private static final long serialVersionUID = 0;
}
