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

/**
 * Injection provider that performs injection into object methods annotate with {@link javax.inject.Inject}
 * This provider correctly resolves and injects object supertypes' methods.
 *
 * @param <T> type of object for wich we perform injection
 */
class MethodInjectingProvider<T> extends MemberInjectingProvider<T> {

    MethodInjectingProvider(Provider<T> delegate, DefaultInjector injector) {
        super(delegate, injector);
    }

    @Override
    protected void injectMembers(T object, Class<?> type) {
        Map<String, List<Method>> methods = collectMethods(type, new LinkedHashMap<>());
        for (List<Method> methodList : methods.values()){
            methodList.forEach(method -> {
                if (method.getAnnotation(Inject.class) != null) {
                    injectMember(object, method);
                }
            });
        }
    }

    /**
     * Collect methods for provided types, including all methods for supertypes
     * wbut without overridden methods.
     *
     * @param type to look up methods
     * @param seenMethods map of already processed methods
     * @return map with all processed methods
     */
    static Map<String, List<Method>> collectMethods(Class<?> type, Map<String, List<Method>> seenMethods) {
        // bail on recursion stop condition
        if (type == Object.class) {
            return seenMethods;
        }

        collectMethods(type.getSuperclass(), seenMethods);

        for (Method method : type.getDeclaredMethods()) {
            // skip static and abstract methods
            if(Modifier.isStatic(method.getModifiers()) || Modifier.isAbstract(method.getModifiers())) {
                continue;
            }
            // calculate method signature
            String methodSignature = getMethodSignature(method);

            // lookup for overridden method
            List<Method> parentMethods = seenMethods.computeIfAbsent(methodSignature, sig -> new ArrayList<>());
            parentMethods.removeIf(parentMethod -> isMethodOverride(parentMethod, method));
            parentMethods.add(method);
        }

        return seenMethods;
    }

    /**
     * Check if method overrides parent (assuming basic check performed by compiler)
     */
    static boolean isMethodOverride(Method parentMethod, Method method) {
        int parentModifier = modifiersToInt(parentMethod.getModifiers());
        if(parentModifier == 0) {
            // no private methods override
            return false;
        }

        // check class package for package private methods
        if(parentModifier == 1) {
            return method.getDeclaringClass().getPackage().equals(parentMethod.getDeclaringClass().getPackage());
        }

        // method has same or broader scope, otherwise java compiler should complain
        return true;
    }

    /**
     * Convert access modifiers to int
     *
     * @param methodModifiers all method modifiers
     * @return 0 - private, 1 - package private, 2 - protected, 3 - public
     */
    static int modifiersToInt(int methodModifiers) {
        if(Modifier.isPrivate(methodModifiers) || Modifier.isStatic(methodModifiers)) {
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

    /**
     * Format method signature in arbitrary form that will return same string for same method name,
     * return type and arguments types.
     * NOTE: result is not in Java convention like "public method()Ljava/lang/Object;"
     *
     * @param method to generate signature for
     * @return method signature
     */
    static String getMethodSignature(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getReturnType().getName()).append(' ').append(method.getName()).append('(');
        for (Type type : method.getGenericParameterTypes()) {
            sb.append(type.getTypeName()).append(',');
        }
        sb.append(')');
        return sb.toString();
    }

    /**
     * Do the injection into method
     *
     * @param object to perform inject at
     * @param method to inject
     */
    private void injectMember(Object object, Method method) {

        Object[] values = arguments(method);

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

    /**
     * @param method to collect arguments for
     * @return values of arguments
     */
    private Object[] arguments(Method method) {

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
