
package io.bootique.di;

/**
 * Maps ClassLoaders to resources. This is a useful abstraction when switching
 * between environments. E.g. between JEE with thread/hierarchical classloaders
 * and OSGi with per-bundle classloaders.
 */
public interface ClassLoaderManager {

    /**
     * Returns a ClassLoader appropriate for loading a given resource. Resource
     * path should be compatible with Class.getResource(..) and such, i.e. the
     * path component separator should be slash, not dot.
     */
    ClassLoader getClassLoader(String resourceName);
}
