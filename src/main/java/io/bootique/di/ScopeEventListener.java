package io.bootique.di;

/**
 * This interface duplicates default reflection based mechanism for receiving DI
 * events. It is not fully supported and its usage are reserved for cases when
 * for some reason it is not possible to use reflection. It is used for example
 * in {@link javax.sql.DataSource} management layer to provide compatibility
 * with java version 5.
 */
public interface ScopeEventListener {

    /**
     * Similar to {@link BeforeScopeEnd}
     */
    void beforeScopeEnd();
}
