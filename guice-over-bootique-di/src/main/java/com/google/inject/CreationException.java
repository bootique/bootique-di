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
import java.util.HashSet;
import java.util.Set;

import com.google.inject.spi.Message;
import com.google.inject.spi.Messages;

/**
 * Thrown when errors occur while creating a {@link Injector}. Includes a list of encountered
 * errors. Clients should catch this exception, log it, and stop execution.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public class CreationException extends RuntimeException {

    private final Set<Message> messages;

    /**
     * Creates a CreationException containing {@code messages}.
     */
    public CreationException(Collection<Message> messages) {
        this.messages = new HashSet<>(messages);
        initCause(Messages.getOnlyCause(this.messages));
    }

    @Override
    public String getMessage() {
        return Messages.formatMessages("Unable to create injector, see the following errors", messages);
    }

    private static final long serialVersionUID = 0;

    public Collection<Message> getErrorMessages() {
        return messages;
    }
}
