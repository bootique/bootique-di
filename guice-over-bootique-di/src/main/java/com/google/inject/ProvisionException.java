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
