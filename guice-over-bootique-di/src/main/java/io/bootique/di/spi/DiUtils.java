package io.bootique.di.spi;

import io.bootique.di.Key;
import io.bootique.di.TypeLiteral;

class DiUtils {

    static <T> io.bootique.di.Key<T> toBootiqueKey(com.google.inject.Key<T> key) {
        return Key.get(toTypeLiteral(key.getTypeLiteral()), key.getAnnotationType());
    }

    static <T> io.bootique.di.Key<T> toBootiqueKey(com.google.inject.TypeLiteral<T> typeLiteral) {
        return Key.get(toTypeLiteral(typeLiteral));
    }

    static <T> io.bootique.di.TypeLiteral<T> toTypeLiteral(com.google.inject.TypeLiteral<T> typeLiteral) {
        return TypeLiteral.of(typeLiteral.getType());
    }

}
