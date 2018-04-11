package io.bootique.di.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Provider;

import io.bootique.di.DIRuntimeException;
import io.bootique.di.Key;
import io.bootique.di.TypeLiteral;

class MethodInjectingProvider<T> extends MemberInjectingProvider<T> {

    MethodInjectingProvider(Provider<T> delegate, DefaultInjector injector) {
        super(delegate, injector);
    }

    @Override
    protected void injectMembers(T object, Class<?> type) {
        injectMembers(object, type, new HashSet<>());
    }

    protected void injectMembers(T object, Class<?> type, Set<String> seenMethods) {
        // bail on recursion stop condition
        if (type == null) {
            return;
        }

        injectMembers(object, type.getSuperclass(), seenMethods);

        for (Method method : type.getDeclaredMethods()) {
            if(!seenMethods.add(formatMethodSignature(method))) {
                continue;
            }
            Inject inject = method.getAnnotation(Inject.class);
            if (inject != null) {
                injectMember(object, method);
            }
        }
    }

    private String formatMethodSignature(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getReturnType().getSimpleName())
                .append(' ')
                .append(method.getName())
                .append('(');
        for(Type type : method.getGenericParameterTypes()) {
            sb.append(type.getTypeName()).append(',');
        }
        sb.append(')');
        return sb.toString();
    }

    private void injectMember(Object object, Method method) {

        Object[] values = values(method);

        method.setAccessible(true);
        try {
            method.invoke(object, values);
        } catch (Exception e) {
            String message = String.format("Error injecting into method '%s.%s()'"
                    , method.getDeclaringClass().getName()
                    , method.getName());
            throw new DIRuntimeException(message, e);
        }
    }

    private Object[] values(Method method) {

        Type[] parameterTypes = method.getGenericParameterTypes();
        Class<?>[] parameterClasses = method.getParameterTypes();
        Object[] result = new Object[parameterTypes.length];
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        InjectionStack stack = injector.getInjectionStack();

        for(int i=0; i<parameterTypes.length; i++) {
            Type parameterType = parameterTypes[i];
            Annotation bindingAnnotation = getQualifier(parameterAnnotations[i], method);

            if (Provider.class.equals(parameterClasses[i])) {
                parameterType = DIUtil.getGenericParameterType(parameterType);
                if (parameterType == null) {
                    throw new DIRuntimeException("Parameter of method '%s.%s()' of 'Provider' type must be "
                            + "parameterized to be usable for injection"
                            , method.getDeclaringClass().getName()
                            , method.getName());
                }

                result[i] = injector.getProvider(Key.get(TypeLiteral.of(parameterType), bindingAnnotation));
            } else {
                Key<?> key = Key.get(TypeLiteral.of(parameterType), bindingAnnotation);
                stack.push(key);
                try {
                    result[i] = injector.getInstance(key);
                } finally {
                    stack.pop();
                }
            }
        }

        return result;
    }
}
