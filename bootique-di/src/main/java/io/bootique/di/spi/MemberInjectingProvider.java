package io.bootique.di.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import javax.inject.Provider;

import io.bootique.di.DIRuntimeException;

abstract class MemberInjectingProvider<T> implements Provider<T> {

    protected final DefaultInjector injector;
    protected final Provider<T> delegate;

    MemberInjectingProvider(Provider<T> delegate, DefaultInjector injector) {
        this.delegate = delegate;
        this.injector = injector;
    }

    @Override
    public T get() {
        T object = delegate.get();
        injectMembers(object, object.getClass());
        return object;
    }

    abstract void injectMembers(T object, Class<?> aClass);

    Annotation getQualifier(Annotation[] annotations, AccessibleObject object) {
        Annotation bindingAnnotation = null;
        for(Annotation fieldAnnotation : annotations) {
            if(DIUtil.isQualifyingAnnotation(fieldAnnotation)) {
                if(bindingAnnotation != null) {
                    throw new DIRuntimeException("Found more than one qualifier annotation for '%s.%s'."
                            , ((Member)object).getDeclaringClass().getName()
                            , ((Member)object).getName());
                }
                bindingAnnotation = fieldAnnotation;
            }
        }
        return bindingAnnotation;
    }

    Annotation getQualifier(AccessibleObject object) {
        return getQualifier(object.getAnnotations(), object);
    }
}
