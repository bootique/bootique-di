
package io.bootique.di.spi;

import io.bootique.di.ClassLoaderManager;

public class DefaultClassLoaderManager implements ClassLoaderManager {

    @Override
    public ClassLoader getClassLoader(String resourceName) {
        // here we are ignoring 'className' when looking for ClassLoader...
        // other implementations (such as OSGi) may actually use it

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        if (classLoader == null) {
            classLoader = DefaultClassLoaderManager.class.getClassLoader();
        }

        // this is too paranoid I guess... "this" class will always have a
        // ClassLoader
        if (classLoader == null) {
            throw new IllegalStateException("Can't find a ClassLoader");
        }

        return classLoader;
    }

}
