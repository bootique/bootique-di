/**
 *    Licensed to the ObjectStyle LLC under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ObjectStyle LLC licenses
 *  this file to you under the Apache License, Version 2.0 (the
 *  “License”); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package com.google.inject;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.inject.spi.Message;

/**
 * Thrown when a programming error such as a misplaced annotation, illegal binding, or unsupported
 * scope is found. Clients should catch this exception, log it, and stop execution.
 *
 * @author jessewilson@google.com (Jesse Wilson)
 * @since 2.0
 */
public final class ConfigurationException extends RuntimeException {

    private final String message;

    private final Set<Message> messages;

    /**
     * Creates a ConfigurationException containing {@code messages}.
     */
    public ConfigurationException(String message) {
        this.message = message;
        this.messages = Collections.singleton(new Message(message));
    }

    public ConfigurationException(Collection<Message> messages) {
        this.message = messages.iterator().next().getMessage();
        this.messages = new HashSet<>(messages);
    }

    @Override
    public String getMessage() {
        return message;
    }

    private static final long serialVersionUID = 0;

    public Collection<Message> getErrorMessages() {
        return messages;
    }
}
