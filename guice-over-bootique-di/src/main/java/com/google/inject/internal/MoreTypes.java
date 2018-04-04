/*
 * Copyright (C) 2008 Google Inc.
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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Objects;

/**
 * Static methods for working with types that we aren't publishing in the public {@code Types} API.
 *
 * @author jessewilson@google.com (Jesse Wilson)
 */
public class MoreTypes {

    private static final Type[] EMPTY_TYPE_ARRAY = new Type[]{};

    private MoreTypes() {
    }

    /**
     * Returns true if {@code type} is free from type variables.
     */
    private static boolean isFullySpecified(Type type) {
        if (type instanceof Class) {
            return true;

        } else if (type instanceof CompositeType) {
            return ((CompositeType) type).isFullySpecified();

        } else if (type instanceof TypeVariable) {
            return false;

        } else {
            return ((CompositeType) canonicalize(type)).isFullySpecified();
        }
    }

    /**
     * Returns a type that is functionally equal but not necessarily equal according to {@link
     * Object#equals(Object) Object.equals()}. The returned type is {@link Serializable}.
     */
    private static Type canonicalize(Type type) {
        if (type instanceof Class) {
            Class<?> c = (Class<?>) type;
            return c.isArray() ? new GenericArrayTypeImpl(canonicalize(c.getComponentType())) : c;

        } else if (type instanceof CompositeType) {
            return type;

        } else if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) type;
            return new ParameterizedTypeImpl(
                    p.getOwnerType(), p.getRawType(), p.getActualTypeArguments());

        } else if (type instanceof GenericArrayType) {
            GenericArrayType g = (GenericArrayType) type;
            return new GenericArrayTypeImpl(g.getGenericComponentType());

        } else if (type instanceof WildcardType) {
            WildcardType w = (WildcardType) type;
            return new WildcardTypeImpl(w.getUpperBounds(), w.getLowerBounds());

        } else {
            // type is either serializable as-is or unsupported
            return type;
        }
    }

    public static Class<?> getRawType(Type type) {
        if (type instanceof Class<?>) {
            // type is a normal class.
            return (Class<?>) type;

        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            // I'm not exactly sure why getRawType() returns Type instead of Class.
            // Neal isn't either but suspects some pathological case related
            // to nested classes exists.
            Type rawType = parameterizedType.getRawType();
            if (!(rawType instanceof Class)) {
                throw new IllegalArgumentException("Expected a Class, but <" + type + "> is of type " + type.getClass().getName());
            }
            return (Class<?>) rawType;

        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return Array.newInstance(getRawType(componentType), 0).getClass();

        } else if (type instanceof TypeVariable || type instanceof WildcardType) {
            // we could use the variable's bounds, but that'll won't work if there are multiple.
            // having a raw type that's more general than necessary is okay
            return Object.class;

        } else {
            throw new IllegalArgumentException(
                    "Expected a Class, ParameterizedType, or "
                            + "GenericArrayType, but <"
                            + type
                            + "> is of type "
                            + type.getClass().getName());
        }
    }

    public static class ParameterizedTypeImpl implements ParameterizedType, Serializable, CompositeType {
        private final Type ownerType;
        private final Type rawType;
        private final Type[] typeArguments;

        public ParameterizedTypeImpl(Type ownerType, Type rawType, Type... typeArguments) {
            // require an owner type if the raw type needs it
            ensureOwnerType(ownerType, rawType);

            this.ownerType = ownerType;
            this.rawType = rawType;
            this.typeArguments = typeArguments.clone();
            for (Type typeArgument : this.typeArguments) {
                Objects.requireNonNull(typeArgument, "type parameter");
                checkNotPrimitive(typeArgument, "type parameters");
            }
        }

        @Override
        public Type[] getActualTypeArguments() {
            return typeArguments.clone();
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return ownerType;
        }

        @Override
        public boolean isFullySpecified() {
            if (ownerType != null && !MoreTypes.isFullySpecified(ownerType)) {
                return false;
            }

            if (!MoreTypes.isFullySpecified(rawType)) {
                return false;
            }

            for (Type type : typeArguments) {
                if (!MoreTypes.isFullySpecified(type)) {
                    return false;
                }
            }

            return true;
        }

        private static void ensureOwnerType(Type ownerType, Type rawType) {
            if (rawType instanceof Class<?>) {
                Class rawTypeAsClass = (Class) rawType;
                if (!(ownerType != null || rawTypeAsClass.getEnclosingClass() == null)) {
                    throw new IllegalArgumentException("No owner type for enclosed " + rawType);
                }
                if (!(ownerType == null || rawTypeAsClass.getEnclosingClass() != null)) {
                    throw new IllegalArgumentException("Owner type for unenclosed " + rawType);
                }
            }
        }

        private static final long serialVersionUID = 0;
    }

    public static class GenericArrayTypeImpl
            implements GenericArrayType, Serializable, CompositeType {
        private final Type componentType;

        GenericArrayTypeImpl(Type componentType) {
            this.componentType = canonicalize(componentType);
        }

        @Override
        public Type getGenericComponentType() {
            return componentType;
        }

        @Override
        public boolean isFullySpecified() {
            return MoreTypes.isFullySpecified(componentType);
        }

        private static final long serialVersionUID = 0;
    }

    /**
     * The WildcardType interface supports multiple upper bounds and multiple lower bounds. We only
     * support what the Java 6 language needs - at most one bound. If a lower bound is set, the upper
     * bound must be Object.class.
     */
    public static class WildcardTypeImpl implements WildcardType, Serializable, CompositeType {
        private final Type upperBound;
        private final Type lowerBound;

        WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
            if (lowerBounds.length > 1) {
                throw new IllegalArgumentException("Must have at most one lower bound.");
            }
            if (upperBounds.length != 1) {
                throw new IllegalArgumentException("Must have exactly one upper bound.");
            }

            if (lowerBounds.length == 1) {
                Objects.requireNonNull(lowerBounds[0], "lowerBound");
                checkNotPrimitive(lowerBounds[0], "wildcard bounds");
                if (upperBounds[0] != Object.class) {
                    throw new IllegalArgumentException("bounded both ways");
                }
                this.lowerBound = canonicalize(lowerBounds[0]);
                this.upperBound = Object.class;

            } else {
                Objects.requireNonNull(upperBounds[0], "upperBound");
                checkNotPrimitive(upperBounds[0], "wildcard bounds");
                this.lowerBound = null;
                this.upperBound = canonicalize(upperBounds[0]);
            }
        }

        @Override
        public Type[] getUpperBounds() {
            return new Type[]{upperBound};
        }

        @Override
        public Type[] getLowerBounds() {
            return lowerBound != null ? new Type[]{lowerBound} : EMPTY_TYPE_ARRAY;
        }

        @Override
        public boolean isFullySpecified() {
            return MoreTypes.isFullySpecified(upperBound)
                    && (lowerBound == null || MoreTypes.isFullySpecified(lowerBound));
        }

        private static final long serialVersionUID = 0;
    }

    private static void checkNotPrimitive(Type type, String use) {
        if (type instanceof Class<?> && ((Class) type).isPrimitive()) {
            throw new IllegalArgumentException("Primitive types are not allowed in " + use + ": " + type);
        }
    }

    /**
     * A type formed from other types, such as arrays, parameterized types or wildcard types
     */
    private interface CompositeType {
        /**
         * Returns true if there are no type variables in this type.
         */
        boolean isFullySpecified();
    }
}
