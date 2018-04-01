
package io.bootique.di;

/**
 * Creates objects for user-provided String class names, injecting dependencies
 * into them.
 */
public interface AdhocObjectFactory {

    /**
     * Returns an instance of "className" that implements "superType", injecting
     * dependencies from the registry into it.
     */
    <T> T newInstance(Class<? super T> superType, String className);

    /**
     * Returns a Java class loaded using ClassLoader returned from
     * {@link ClassLoaderManager#getClassLoader(String)} for a given class name.
     */
    Class<?> getJavaClass(String className);
}
