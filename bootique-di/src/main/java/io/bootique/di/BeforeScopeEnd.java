package io.bootique.di;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used by objects that want to receive scope ending events from the DI
 * registry.
 * <p>
 * Annotated method must be public and have no parameters. Return type is ignored by the
 * event dispatcher.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BeforeScopeEnd {

}
