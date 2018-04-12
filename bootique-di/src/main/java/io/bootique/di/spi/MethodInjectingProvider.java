package io.bootique.di.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        Map<String, List<Method>> methods = new LinkedHashMap<>();
        collectMethods(type, methods);

        for (List<Method> methodList : methods.values()){
            for(Method method : methodList) {
                Inject inject = method.getAnnotation(Inject.class);
                if (inject != null) {
                    injectMember(object, method);
                }
            }
        }
    }

    private void collectMethods(Class<?> type, Map<String, List<Method>> seenMethods) {
        // bail on recursion stop condition
        if (type == Object.class) {
            return;
        }

        collectMethods(type.getSuperclass(), seenMethods);

        for (Method method : type.getDeclaredMethods()) {
            // skip static and abstract methods
            if(Modifier.isStatic(method.getModifiers()) || Modifier.isAbstract(method.getModifiers())) {
                continue;
            }
            // calculate method signature
            String methodSignature = formatMethodSignature(method);

            // lookup for overridden method
            List<Method> overriddenMethods = seenMethods.computeIfAbsent(methodSignature, s -> new ArrayList<>());
            overriddenMethods.removeIf(overriddenMethod -> isMethodOverride(overriddenMethod, method));
            overriddenMethods.add(method);
        }
    }

    /**
     * Check if method overrides parent (assuming basic check performed by compiler)
     */
    private boolean isMethodOverride(Method parentMethod, Method method) {
        // method has same or broader scope
        int parentModifier = modifiersToInt(parentMethod.getModifiers());
        if(parentModifier == 0) {
            // no private methods override
            return false;
        }
        if(parentModifier == 1) {
            return method.getDeclaringClass().getPackage().equals(parentMethod.getDeclaringClass().getPackage());
        }
        return true;
    }

    private int modifiersToInt(int methodModifiers) {
        if(Modifier.isPrivate(methodModifiers)) {
            return 0;
        }
        if(Modifier.isProtected(methodModifiers)) {
            return 2;
        }
        if(Modifier.isPublic(methodModifiers)) {
            return 3;
        }

        // package private
        return 1;
    }

    private String formatMethodSignature(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getReturnType().getName()).append(' ').append(method.getName()).append('(');
        for (Type type : method.getGenericParameterTypes()) {
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

        for (int i = 0; i < parameterTypes.length; i++) {
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
