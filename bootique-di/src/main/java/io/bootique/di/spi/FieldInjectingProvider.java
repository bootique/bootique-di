package io.bootique.di.spi;

import io.bootique.di.DIRuntimeException;
import io.bootique.di.Key;
import io.bootique.di.TypeLiteral;

import javax.inject.Inject;
import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

class FieldInjectingProvider<T> extends MemberInjectingProvider<T> {

    FieldInjectingProvider(Provider<T> delegate, DefaultInjector injector) {
        super(delegate, injector);
    }

    @Override
    protected void injectMembers(T object, Class<?> type) {

        // bail on recursion stop condition
        if (type == Object.class) {
            return;
        }

        injectMembers(object, type.getSuperclass());

        for (Field field : type.getDeclaredFields()) {
            if(Modifier.isStatic(field.getModifiers())) {
                // skip static fields completely
                continue;
            }
            Inject inject = field.getAnnotation(Inject.class);
            if (inject != null) {
                injectMember(object, field, getQualifier(field));
            }
        }
    }

    private void injectMember(Object object, Field field, Annotation bindingAnnotation) {

        Object value = value(field, bindingAnnotation);

        field.setAccessible(true);
        try {
            field.set(object, value);
        } catch (Exception e) {
            String message = String.format("Error injecting into field %s.%s of type %s", field.getDeclaringClass()
                    .getName(), field.getName(), field.getType().getName());
            throw new DIRuntimeException(message, e);
        }
    }

    protected Object value(Field field, Annotation bindingAnnotation) {

        Class<?> fieldType = field.getType();
        InjectionStack stack = injector.getInjectionStack();

        if (Provider.class.equals(fieldType)) {
            Type parameterType = DIUtil.getGenericParameterType(field.getGenericType());

            if (parameterType == null) {
                throw new DIRuntimeException("Provider field %s.%s of type %s must be "
                        + "parameterized to be usable for injection", field.getDeclaringClass().getName(),
                        field.getName(), fieldType.getName());
            }

            return injector.getProvider(Key.get(TypeLiteral.of(parameterType), bindingAnnotation));
        } else {
            Key<?> key = Key.get(TypeLiteral.of(field.getGenericType()), bindingAnnotation);
            stack.push(key);
            try {
                return injector.getInstance(key);
            } finally {
                stack.pop();
            }
        }
    }
}
