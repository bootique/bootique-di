package io.bootique.di;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({
        FIELD, CONSTRUCTOR
})
public @interface Inject {

    /**
     * An optional name of the dependency for injecting dependency types that have
     * multiple bindings in the container.
     */
    String value() default "";
}
