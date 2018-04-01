package io.bootique.di.spi;

import io.bootique.di.DIRuntimeException;
import io.bootique.di.Inject;
import io.bootique.di.Key;

import javax.inject.Provider;
import java.lang.reflect.Field;

class FieldInjectingProvider<T> implements Provider<T> {

    private DefaultInjector injector;
    private Provider<T> delegate;

    FieldInjectingProvider(Provider<T> delegate, DefaultInjector injector) {
        this.delegate = delegate;
        this.injector = injector;
    }

    @Override
    public T get() {
        T object = delegate.get();
        injectMembers(object, object.getClass());
        return object;
    }

    private void injectMembers(T object, Class<?> type) {

        // bail on recursion stop condition
        if (type == null) {
            return;
        }

        for (Field field : type.getDeclaredFields()) {

            Inject inject = field.getAnnotation(Inject.class);
            if (inject != null) {
                injectMember(object, field, inject.value());
            }
        }

        injectMembers(object, type.getSuperclass());
    }

    private void injectMember(Object object, Field field, String bindingName) {

        Object value = value(field, bindingName);

        field.setAccessible(true);
        try {
            field.set(object, value);
        } catch (Exception e) {
            String message = String.format("Error injecting into field %s.%s of type %s", field.getDeclaringClass()
                    .getName(), field.getName(), field.getType().getName());
            throw new DIRuntimeException(message, e);
        }
    }

    protected Object value(Field field, String bindingName) {

        Class<?> fieldType = field.getType();
        InjectionStack stack = injector.getInjectionStack();

        if (Provider.class.equals(fieldType)) {

            Class<?> objectClass = DIUtil.parameterClass(field.getGenericType());

            if (objectClass == null) {
                throw new DIRuntimeException("Provider field %s.%s of type %s must be "
                        + "parameterized to be usable for injection", field.getDeclaringClass().getName(),
                        field.getName(), fieldType.getName());
            }

            return injector.getProvider(Key.get(objectClass, bindingName));
        } else {
            Key<?> key = DIUtil.getKeyForTypeAndGenericType(fieldType, field.getGenericType(), bindingName);
            stack.push(key);
            try {
                return injector.getInstance(key);
            } finally {
                stack.pop();
            }
        }
    }
}
