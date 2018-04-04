/*
 * Copyright (C) 2017 Google Inc.
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

import java.util.Collection;
import java.util.Formatter;
import java.util.List;

/**
 * Utility methods for {@link Message} objects
 */
public final class Messages {
    private Messages() {
    }

    /**
     * Returns the cause throwable if there is exactly one cause in {@code messages}. If there are
     * zero or multiple messages with causes, null is returned.
     */
    public static Throwable getOnlyCause(Collection<Message> messages) {
        Throwable onlyCause = null;
        for (Message message : messages) {
            Throwable messageCause = message.getCause();
            if (messageCause == null) {
                continue;
            }

            if (onlyCause != null) {
                return null;
            }

            onlyCause = messageCause;
        }

        return onlyCause;
    }


    /**
     * Returns the formatted message for an exception with the specified messages.
     */
    public static String formatMessages(String heading, Collection<Message> errorMessages) {
        Formatter fmt = new Formatter().format(heading).format(":%n%n");
        int index = 1;
        boolean displayCauses = getOnlyCause(errorMessages) == null;

        for (Message errorMessage : errorMessages) {
            int thisIdx = index++;
            fmt.format("%s) %s%n", thisIdx, errorMessage.getMessage());

            List<Object> dependencies = errorMessage.getSources();
            for (int i = dependencies.size() - 1; i >= 0; i--) {
                Object source = dependencies.get(i);
                fmt.format("  at %s", source);
            }

            Throwable cause = errorMessage.getCause();
            if (displayCauses && cause != null) {
                fmt.format("Caused by: %s", cause);
            }

            fmt.format("%n");
        }

        if (errorMessages.size() == 1) {
            fmt.format("1 error");
        } else {
            fmt.format("%s errors", errorMessages.size());
        }

        return fmt.toString();
    }

}
