package io.bootique.di.spi;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.*;

public class MethodInjectingProviderTest {

    @Test
    public void collectMethods() {
        Map<String, List<Method>> methods = MethodInjectingProvider
                .collectMethods(Class2.class, new LinkedHashMap<>());

        assertEquals(3, methods.size());

        String[] methodSig = {"int methodInt()", "void methodArgs(int,java.lang.Object,)", "void method1()"};
        for(String sig : methodSig) {
            assertTrue("No method " + sig, methods.containsKey(sig));
        }

        List<Method> methodList = methods.get(methodSig[0]);
        assertEquals(1, methodList.size());
        assertEquals(Class2.class, methodList.get(0).getDeclaringClass());

        methodList = methods.get(methodSig[1]);
        assertEquals(2, methodList.size());
        assertEquals(Class1.class, methodList.get(0).getDeclaringClass());
        assertEquals(Class2.class, methodList.get(1).getDeclaringClass());

        methodList = methods.get(methodSig[2]);
        assertEquals(1, methodList.size());
        assertEquals(Class2.class, methodList.get(0).getDeclaringClass());
    }

    @Test
    public void testGetMethodSignature() throws Exception {
        Method methodInt = Class1.class.getDeclaredMethod("methodInt");
        Method methodArgs = Class1.class.getDeclaredMethod("methodArgs", int.class, Object.class);

        String signature1 = MethodInjectingProvider.getMethodSignature(methodInt);
        assertEquals("int methodInt()", signature1);

        String signature2 = MethodInjectingProvider.getMethodSignature(methodArgs);
        assertEquals("void methodArgs(int,java.lang.Object,)", signature2);
    }

    static class Class1 {

        static void method() {
        }

        public void method1() {
        }

        int methodInt() {
            return 0;
        }

        private void methodArgs(int i, Object obj) {
        }
    }

    static class Class2 extends Class1 {

        static void method() {
        }

        @Override
        public void method1() {
        }

        @Override
        int methodInt() {
            return 1;
        }

        private void methodArgs(int i, Object obj) {
        }
    }
}