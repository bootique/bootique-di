package io.bootique.di.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import io.bootique.di.DIBootstrap;
import io.bootique.di.Provides;

/**
 * Collection of predicates used internally by injector.
 *
 * @see DIBootstrap#injectorBuilder() methods to customize predicates.
 */
public class InjectorPredicates {

    // Default predicates, based on javax.inject
    private Predicate<AccessibleObject> injectPredicate = o -> o.isAnnotationPresent(Inject.class);
    private Predicate<Method> providesMethodPredicate = m -> m.isAnnotationPresent(Provides.class);
    private Predicate<AnnotatedElement> singletonPredicate = o -> o.isAnnotationPresent(Singleton.class);
    private Predicate<Class<? extends Annotation>> qualifierPredicate = c -> c.isAnnotationPresent(Qualifier.class);
    private Predicate<Type> providerPredicate = Provider.class::equals;

    private Function<Provider<?>, Provider<?>> providerFunction = Function.identity();

    public InjectorPredicates() {
    }

    public void setInjectPredicate(Predicate<AccessibleObject> injectPredicate) {
        this.injectPredicate = injectPredicate;
    }

    public void setProviderPredicate(Predicate<Type> providerPredicate) {
        this.providerPredicate = providerPredicate;
    }

    public void setProvidesMethodPredicate(Predicate<Method> providesMethodPredicate) {
        this.providesMethodPredicate = providesMethodPredicate;
    }

    public void setQualifierPredicate(Predicate<Class<? extends Annotation>> qualifierPredicate) {
        this.qualifierPredicate = qualifierPredicate;
    }

    public void setSingletonPredicate(Predicate<AnnotatedElement> singletonPredicate) {
        this.singletonPredicate = singletonPredicate;
    }

    @SuppressWarnings("unchecked")
    public <T> void setProviderFunction(Function<Provider<T>, Provider<T>> providerFunction) {
        this.providerFunction = (Function)providerFunction;
    }

    boolean isSingleton(AnnotatedElement object) {
        return singletonPredicate.test(object);
    }

    boolean haveInjectAnnotation(AccessibleObject object) {
        return injectPredicate.test(object);
    }

    boolean isProviderMethod(Method method) {
        return providesMethodPredicate.test(method);
    }

    boolean isQualifierAnnotation(Annotation annotation) {
        return qualifierPredicate.test(annotation.annotationType());
    }

    boolean isProviderType(Type type) {
        return providerPredicate.test(type);
    }

    @SuppressWarnings("unchecked")
    <T> Provider<T> wrapProvider(Provider<T> provider) {
        return (Provider<T>)providerFunction.apply(provider);
    }

    Predicate<Method> getProvidesMethodPredicate() {
        return providesMethodPredicate;
    }

    Predicate<AccessibleObject> getInjectPredicate() {
        return injectPredicate;
    }

    Predicate<Class<? extends Annotation>> getQualifierPredicate() {
        return qualifierPredicate;
    }
}
