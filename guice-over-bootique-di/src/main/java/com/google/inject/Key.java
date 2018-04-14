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

package com.google.inject;

import static com.google.inject.internal.Annotations.generateAnnotation;
import static com.google.inject.internal.Annotations.isAllDefaultMethods;

import com.google.inject.internal.Annotations;
import io.bootique.di.spi.DiUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Binding key consisting of an injection type and an optional annotation. Matches the type and
 * annotation at a point of injection.
 *
 * <p>For example, {@code Key.get(Service.class, Transactional.class)} will match:
 *
 * <pre>
 *   {@literal @}Inject
 *   public void setService({@literal @}Transactional Service service) {
 *     ...
 *   }
 * </pre>
 *
 * <p>{@code Key} supports generic types via subclassing just like {@link TypeLiteral}.
 *
 * <p>Keys do not differentiate between primitive types (int, char, etc.) and their corresponding
 * wrapper types (Integer, Character, etc.). Primitive types will be replaced with their wrapper
 * types when keys are created.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public class Key<T> {

    private final AnnotationStrategy annotationStrategy;

    private final TypeLiteral<T> typeLiteral;

    /**
     * Unsafe. Constructs a key from a manually specified type.
     */
    @SuppressWarnings("unchecked")
    private Key(Type type, AnnotationStrategy annotationStrategy) {
        this((TypeLiteral<T>) TypeLiteral.get(type), annotationStrategy);
    }

    /**
     * Constructs a key from a manually specified type.
     */
    private Key(TypeLiteral<T> typeLiteral, AnnotationStrategy annotationStrategy) {
        this.annotationStrategy = annotationStrategy;
        this.typeLiteral = typeLiteral;
    }

    /**
     * Gets the key type.
     */
    public final TypeLiteral<T> getTypeLiteral() {
        return typeLiteral;
    }

    /**
     * Gets the annotation type.
     */
    public final Class<? extends Annotation> getAnnotationType() {
        return annotationStrategy.getAnnotationType();
    }

    /**
     * Gets the annotation.
     */
    public final Annotation getAnnotation() {
        return annotationStrategy.getAnnotation();
    }

    /**
     * Gets a key for an injection type.
     */
    public static <T> Key<T> get(Class<T> type) {
        return new Key<>(type, NullAnnotationStrategy.INSTANCE);
    }

    /**
     * Gets a key for an injection type and an annotation type.
     */
    public static <T> Key<T> get(Class<T> type, Class<? extends Annotation> annotationType) {
        return new Key<>(type, strategyFor(annotationType));
    }

    /**
     * Gets a key for an injection type and an annotation.
     */
    public static <T> Key<T> get(Class<T> type, Annotation annotation) {
        return new Key<>(type, strategyFor(annotation));
    }

    /**
     * Gets a key for an injection type.
     */
    public static Key<?> get(Type type) {
        return new Key<>(type, NullAnnotationStrategy.INSTANCE);
    }

    /**
     * Gets a key for an injection type and an annotation type.
     */
    public static Key<?> get(Type type, Class<? extends Annotation> annotationType) {
        return new Key<>(type, strategyFor(annotationType));
    }

    /**
     * Gets a key for an injection type and an annotation.
     */
    public static Key<?> get(Type type, Annotation annotation) {
        return new Key<>(type, strategyFor(annotation));
    }

    /**
     * Gets a key for an injection type.
     */
    public static <T> Key<T> get(TypeLiteral<T> typeLiteral) {
        return new Key<>(typeLiteral, NullAnnotationStrategy.INSTANCE);
    }

    /**
     * Gets a key for an injection type and an annotation type.
     */
    public static <T> Key<T> get(
            TypeLiteral<T> typeLiteral, Class<? extends Annotation> annotationType) {
        return new Key<>(typeLiteral, strategyFor(annotationType));
    }

    /**
     * Gets a key for an injection type and an annotation.
     */
    public static <T> Key<T> get(TypeLiteral<T> typeLiteral, Annotation annotation) {
        return new Key<>(typeLiteral, strategyFor(annotation));
    }

    private interface AnnotationStrategy {
        Annotation getAnnotation();
        Class<? extends Annotation> getAnnotationType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key<?> key = (Key<?>) o;
        return DiUtils.toBootiqueKey(this).equals(DiUtils.toBootiqueKey(key));
    }

    @Override
    public int hashCode() {
        return DiUtils.toBootiqueKey(this).hashCode();
    }

    /**
     * Gets the strategy for an annotation.
     */
    private static AnnotationStrategy strategyFor(Annotation annotation) {
        Objects.requireNonNull(annotation);
        Class<? extends Annotation> annotationType = annotation.annotationType();
        ensureRetainedAtRuntime(annotationType);
        ensureIsBindingAnnotation(annotationType);

        if (Annotations.isMarker(annotationType)) {
            return new AnnotationTypeStrategy(annotationType, annotation);
        }

        return new AnnotationInstanceStrategy(Annotations.canonicalizeIfNamed(annotation));
    }

    /**
     * Gets the strategy for an annotation type.
     */
    private static AnnotationStrategy strategyFor(Class<? extends Annotation> annotationType) {
        annotationType = Annotations.canonicalizeIfNamed(annotationType);
        if (isAllDefaultMethods(annotationType)) {
            return strategyFor(generateAnnotation(annotationType));
        }

        Objects.requireNonNull(annotationType);
        ensureRetainedAtRuntime(annotationType);
        ensureIsBindingAnnotation(annotationType);
        return new AnnotationTypeStrategy(annotationType, null);
    }

    private static void ensureRetainedAtRuntime(Class<? extends Annotation> annotationType) {
        if (!Annotations.isRetainedAtRuntime(annotationType)) {
            throw new IllegalArgumentException(annotationType.getName()
                    + " is not retained at runtime. Please annotate it with @Retention(RUNTIME).");
        }
    }

    private static void ensureIsBindingAnnotation(Class<? extends Annotation> annotationType) {
        if (!Annotations.isBindingAnnotation(annotationType)) {
            throw new IllegalArgumentException(annotationType.getName()
                    + " is not a binding annotation. Please annotate it with @BindingAnnotation.");
        }
    }

    enum NullAnnotationStrategy implements AnnotationStrategy {
        INSTANCE;

        @Override
        public Annotation getAnnotation() {
            return null;
        }

        @Override
        public Class<? extends Annotation> getAnnotationType() {
            return null;
        }
    }

    // this class not test-covered
    static class AnnotationInstanceStrategy implements AnnotationStrategy {

        final Annotation annotation;

        AnnotationInstanceStrategy(Annotation annotation) {
            this.annotation = Objects.requireNonNull(annotation);
        }

        @Override
        public Annotation getAnnotation() {
            return annotation;
        }

        @Override
        public Class<? extends Annotation> getAnnotationType() {
            return annotation.annotationType();
        }
    }

    static class AnnotationTypeStrategy implements AnnotationStrategy {

        final Class<? extends Annotation> annotationType;

        // Keep the instance around if we have it so the client can request it.
        final Annotation annotation;

        AnnotationTypeStrategy(Class<? extends Annotation> annotationType, Annotation annotation) {
            this.annotationType = Objects.requireNonNull(annotationType);
            this.annotation = annotation;
        }

        @Override
        public Annotation getAnnotation() {
            return annotation;
        }

        @Override
        public Class<? extends Annotation> getAnnotationType() {
            return annotationType;
        }
    }
}