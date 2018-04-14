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

package com.google.inject.internal;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.inject.BindingAnnotation;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Qualifier;

/**
 * Annotation utilities.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public class Annotations {

    /**
     * Returns {@code true} if the given annotation type has no attributes.
     */
    public static boolean isMarker(Class<? extends Annotation> annotationType) {
        return annotationType.getDeclaredMethods().length == 0;
    }

    public static boolean isAllDefaultMethods(Class<? extends Annotation> annotationType) {
        boolean hasMethods = false;
        for (Method m : annotationType.getDeclaredMethods()) {
            hasMethods = true;
            if (m.getDefaultValue() == null) {
                return false;
            }
        }
        return hasMethods;
    }

    /** Gets a key for the given type, member and annotations. */
    public static Key<?> getKey(TypeLiteral<?> type, Member member, Annotation[] annotations, Errors errors) throws ErrorsException {
        int numErrorsBefore = errors.size();
        Annotation found = findBindingAnnotation(errors, member, annotations);
        errors.throwIfNewErrors(numErrorsBefore);
        return found == null ? Key.get(type) : Key.get(type, found);
    }

    /** Returns the binding annotation on {@code member}, or null if there isn't one. */
    public static Annotation findBindingAnnotation(Errors errors, Member member, Annotation[] annotations) {
        Annotation found = null;
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (isBindingAnnotation(annotationType)) {
                if (found != null) {
                    errors.duplicateBindingAnnotations(member, found.annotationType(), annotationType);
                } else {
                    found = annotation;
                }
            }
        }

        return found;
    }

    // TODO: Guice cache had weak keys..
    private static final ConcurrentHashMap<Class<? extends Annotation>, Annotation> cache = new ConcurrentHashMap<>();

    /**
     * Generates an Annotation for the annotation class. Requires that the annotation is all
     * optionals.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T generateAnnotation(Class<T> annotationType) {
        if (!isAllDefaultMethods(annotationType)) {
            throw new IllegalArgumentException(annotationType + " is not all default methods");
        }
        return (T) cache.computeIfAbsent(annotationType, Annotations::generateAnnotationImpl);
    }

    private static <T extends Annotation> T generateAnnotationImpl(final Class<T> annotationType) {
        final Map<String, Object> members = resolveMembers(annotationType);
        return annotationType.cast(
                Proxy.newProxyInstance(
                        annotationType.getClassLoader(),
                        new Class<?>[]{annotationType}, (proxy, method, args) -> {
                            String name = method.getName();
                            switch (name) {
                                case "annotationType":
                                    return annotationType;
                                case "toString":
                                    return annotationToString(annotationType, members);
                                case "hashCode":
                                    return annotationHashCode(annotationType, members);
                                case "equals":
                                    return annotationEquals(annotationType, members, args[0]);
                                default:
                                    return members.get(name);
                            }
                        }));
    }

    private static Map<String, Object> resolveMembers(Class<? extends Annotation> annotationType) {
        Map<String, Object> result = new HashMap<>();
        for (Method method : annotationType.getDeclaredMethods()) {
            result.put(method.getName(), method.getDefaultValue());
        }
        return Collections.unmodifiableMap(result);
    }

    /**
     * Implements {@link Annotation#equals}.
     */
    private static boolean annotationEquals(
            Class<? extends Annotation> type, Map<String, Object> members, Object other)
            throws Exception {
        if (!type.isInstance(other)) {
            return false;
        }
        for (Method method : type.getDeclaredMethods()) {
            String name = method.getName();
            if (!Arrays.deepEquals(
                    new Object[]{method.invoke(other)}, new Object[]{members.get(name)})) {
                return false;
            }
        }
        return true;
    }

    /**
     * Implements {@link Annotation#hashCode}.
     */
    private static int annotationHashCode(
            Class<? extends Annotation> type, Map<String, Object> members) {
        int result = 0;
        for (Method method : type.getDeclaredMethods()) {
            String name = method.getName();
            Object value = members.get(name);
            result += (127 * name.hashCode()) ^ (Arrays.deepHashCode(new Object[]{value}) - 31);
        }
        return result;
    }

    /**
     * Implements {@link Annotation#toString}.
     */
    private static String annotationToString(Class<? extends Annotation> type, Map<String, Object> members) {
        StringBuilder sb = new StringBuilder().append("@").append(type.getName()).append("(");

        // TODO: is it debug only?
        //JOINER.appendTo(sb, Maps.transformValues(members, DEEP_TO_STRING_FN));
        return sb.append(")").toString();
    }

    /**
     * Returns true if the given annotation is retained at runtime.
     */
    public static boolean isRetainedAtRuntime(Class<? extends Annotation> annotationType) {
        Retention retention = annotationType.getAnnotation(Retention.class);
        return retention != null && retention.value() == RetentionPolicy.RUNTIME;
    }

    private static final boolean QUOTE_MEMBER_VALUES = determineWhetherToQuote();

    /**
     * Returns {@code value}, quoted if annotation implementations quote their member values. In Java
     * 9, annotations quote their string members.
     */
    public static String memberValueString(String value) {
        return QUOTE_MEMBER_VALUES ? "\"" + value + "\"" : value;
    }

    @Retention(RUNTIME)
    private @interface TestAnnotation {
        String value();
    }

    @TestAnnotation("determineWhetherToQuote")
    private static boolean determineWhetherToQuote() {
        try {
            String annotation =
                    Annotations.class
                            .getDeclaredMethod("determineWhetherToQuote")
                            .getAnnotation(TestAnnotation.class)
                            .toString();
            return annotation.contains("\"determineWhetherToQuote\"");
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Checks for the presence of annotations. Caches results because Android doesn't.
     */
    static class AnnotationChecker {
        private final Collection<Class<? extends Annotation>> annotationTypes;

        final ConcurrentHashMap<Class<? extends Annotation>, Boolean> cache = new ConcurrentHashMap<>();

        /**
         * Constructs a new checker that looks for annotations of the given types.
         */
        AnnotationChecker(Collection<Class<? extends Annotation>> annotationTypes) {
            this.annotationTypes = annotationTypes;
        }

        /**
         * Returns true if the given type has one of the desired annotations.
         */
        boolean hasAnnotations(Class<? extends Annotation> annotated) {
            return cache.computeIfAbsent(annotated, a -> {
                for (Annotation annotation : a.getAnnotations()) {
                    if (annotationTypes.contains(annotation.annotationType())) {
                        return true;
                    }
                }
                return false;
            });
        }
    }

    private static final AnnotationChecker bindingAnnotationChecker =
            new AnnotationChecker(Arrays.asList(BindingAnnotation.class, Qualifier.class));

    /**
     * Returns true if annotations of the specified type are binding annotations.
     */
    public static boolean isBindingAnnotation(Class<? extends Annotation> annotationType) {
        return bindingAnnotationChecker.hasAnnotations(annotationType);
    }

    /**
     * If the annotation is an instance of {@code javax.inject.Named}, canonicalizes to
     * com.google.guice.name.Named. Returns the given annotation otherwise.
     */
    public static Annotation canonicalizeIfNamed(Annotation annotation) {
        if (annotation instanceof javax.inject.Named) {
            return Names.named(((javax.inject.Named) annotation).value());
        } else {
            return annotation;
        }
    }

    /**
     * If the annotation is the class {@code javax.inject.Named}, canonicalizes to
     * com.google.guice.name.Named. Returns the given annotation class otherwise.
     */
    public static Class<? extends Annotation> canonicalizeIfNamed(
            Class<? extends Annotation> annotationType) {
        if (annotationType == javax.inject.Named.class) {
            return Named.class;
        } else {
            return annotationType;
        }
    }
}
