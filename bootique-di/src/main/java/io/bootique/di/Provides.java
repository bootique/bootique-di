package io.bootique.di;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for the compiler to generate source code for the provider method.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface Provides {
}
