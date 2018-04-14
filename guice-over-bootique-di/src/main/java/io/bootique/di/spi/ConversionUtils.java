package io.bootique.di.spi;

import java.lang.annotation.Annotation;

import com.google.inject.name.Names;

/**
 * Guice &lt;-&gt; Bootique DI conversion utils
 */
public final class ConversionUtils {

    private ConversionUtils() {
    }

    public static <T> io.bootique.di.Key<T> toBootiqueKey(com.google.inject.Key<T> key) {
        return io.bootique.di.Key.get(toTypeLiteral(key.getTypeLiteral()), key.getAnnotationType());
    }

    public static <T> io.bootique.di.Key<T> toBootiqueKey(com.google.inject.TypeLiteral<T> typeLiteral) {
        return io.bootique.di.Key.get(toTypeLiteral(typeLiteral));
    }

    public static <T> com.google.inject.Key<T> toGuiceKey(com.google.inject.TypeLiteral<T> typeLiteral, io.bootique.di.Key<T> bootiqueKey) {
        String name = bootiqueKey.getBindingName();
        if(name != null) {
            return com.google.inject.Key.get(typeLiteral, Names.named(name));
        }

        Class<? extends Annotation> annotationType = bootiqueKey.getBindingAnnotation();
        if(annotationType != null) {
            return com.google.inject.Key.get(typeLiteral, annotationType);
        }

        return com.google.inject.Key.get(typeLiteral);
    }

    public static <T> io.bootique.di.TypeLiteral<T> toTypeLiteral(com.google.inject.TypeLiteral<T> typeLiteral) {
        return io.bootique.di.TypeLiteral.of(typeLiteral.getType());
    }

}
