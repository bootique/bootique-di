package io.bootique.di.spi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A non-public event annotation used by scope object providers to unregister objects
 * created within a scope. Registry objects within a given scope will never be able to
 * receive this event, so never annotate custom objects with this.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@interface AfterScopeEnd {

}
