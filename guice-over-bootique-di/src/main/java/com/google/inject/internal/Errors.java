/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.google.inject.internal;

import com.google.inject.spi.Message;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A collection of error messages. If this type is passed as a method parameter, the method is
 * considered to have executed successfully only if new errors were not added to this collection.
 *
 * @author jessewilson@google.com (Jesse Wilson)
 */
public final class Errors implements Serializable {

    /**
     * The root errors object. Used to access the list of error messages.
     */
    private final Errors root;

    /**
     * null unless (root == this) and error messages exist. Never an empty list.
     */
    private List<Message> errors; // lazy, use getErrorsForAdd()

    public Errors(Object source) {
        this.root = this;
    }

    Errors duplicateBindingAnnotations(Member member, Class<? extends Annotation> a, Class<? extends Annotation> b) {
        return addMessage(
                "%s has more than one annotation annotated with @BindingAnnotation: %s and %s",
                member, a, b);
    }

    private Errors merge(Collection<Message> messages) {
        for (Message message : messages) {
            addMessage(message);
        }
        return this;
    }

    public Errors merge(Errors moreErrors) {
        if (moreErrors.root == root || moreErrors.root.errors == null) {
            return this;
        }

        merge(moreErrors.root.errors);
        return this;
    }


    void throwIfNewErrors(int expectedSize) throws ErrorsException {
        if (size() == expectedSize) {
            return;
        }

        throw toException();
    }

    private ErrorsException toException() {
        return new ErrorsException(this);
    }

    private Errors addMessage(String messageFormat, Object... arguments) {
        return addMessage(new Message(String.format(messageFormat, arguments)));
    }

    private Errors addMessage(Message message) {
        if (root.errors == null) {
            root.errors = new ArrayList<>();
        }
        root.errors.add(message);
        return this;
    }

    public List<Message> getMessages() {
        if (root.errors == null) {
            return Collections.emptyList();
        }

        return root.errors;
    }

    public int size() {
        return root.errors == null ? 0 : root.errors.size();
    }

}
