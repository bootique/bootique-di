package io.bootique.di;

import java.lang.annotation.Annotation;

/**
 * An object passed to a {@link Module} by the DI container during initialization, that
 * provides the API for the module to bind its services to the container. Note that the
 * default {@link Scope} of the bound objects is normally "singleton" and can be changed
 * to "no scope" or a custom scope via a corresponding method of a binding builder. E.g.
 * see {@link BindingBuilder#in(Scope)}.
 */
public interface Binder {

    //----------------------
    //   Simple bindings
    //----------------------

    /**
     * Starts an unnamed binding of a specific interface. Binding should continue using
     * returned BindingBuilder.
     */
    <T> BindingBuilder<T> bind(Class<T> interfaceType);

    /**
     * Starts a binding of a specific interface based on a provided binding key. This
     * method is more generic than {@link #bind(Class)} and allows to create named
     * bindings in addition to default ones. Binding should continue using returned
     * BindingBuilder.
     */
    <T> BindingBuilder<T> bind(Key<T> key);

    //----------------------
    //   Optional bindings
    //----------------------

    /**
     * Starts an unnamed optional binding of a specific interface.
     * Binding should continue using returned BindingBuilder.
     */
    <T> BindingBuilder<T> bindOptional(Class<T> interfaceType);

    /**
     * Starts an optional binding of a specific interface based on a provided binding key.
     * This method is more generic than {@link #bind(Class)} and allows to create qualified
     * bindings in addition to default ones.
     * Binding should continue using returned BindingBuilder.
     */
    <T> BindingBuilder<T> bindOptional(Key<T> key);

    //----------------------
    //   Bindings override
    //----------------------

    /**
     *
     * Start override binding of a specific interface.
     *
     * @param interfaceType to override
     * @param <T> binding type
     * @return binding builder to continue override
     */
    <T> BindingBuilder<T> override(Class<T> interfaceType);

    /**
     *
     * Start override binding of a specific interface based on a provided binding key.
     *
     * @param key to override
     * @param <T> binding type
     * @return binding builder to continue override
     */
    <T> BindingBuilder<T> override(Key<T> key);

    //----------------------
    //  Map<K,V> bindings
    //----------------------

    /**
     * Starts a binding of a java.util.Map&lt;K, V&gt; distinguished by its
     * keys type, values type and qualifier annotation.
     * Map binding should continue using returned MapBuilder.
     * This is a type safe way of binding a map.
     */
    <K, V> MapBuilder<K, V> bindMap(Class<K> keyType, Class<V> valueType, Class<? extends Annotation> qualifier);

    /**
     * Starts a binding of a java.util.Map&lt;K, V&gt; distinguished by its
     * keys type, values type and binding name.
     * Map binding should continue using returned MapBuilder.
     * This is a type safe way of binding a map.
     */
    <K, V> MapBuilder<K, V> bindMap(Class<K> keyType, Class<V> valueType, String bindingName);

    /**
     * Starts a binding of a java.util.Map&lt;K, V&gt; distinguished by its keys and values type.
     * Map binding should continue using returned MapBuilder.
     * This is a type safe way of binding a map.
     */
    <K, V> MapBuilder<K, V> bindMap(Class<K> keyType, Class<V> valueType);

    /**
     * Starts a binding of a java.util.Map&lt;K, V&gt; distinguished by its
     * keys type, values type and qualifier annotation.
     * Map binding should continue using returned MapBuilder.
     * This is a type safe way of binding a map.
     */
    <K, V> MapBuilder<K, V> bindMap(TypeLiteral<K> keyType, TypeLiteral<V> valueType, Class<? extends Annotation> qualifier);

    /**
     * Starts a binding of a java.util.Map&lt;K, V&gt; distinguished by its
     * keys type, values type and binding name.
     * Map binding should continue using returned MapBuilder.
     * This is a type safe way of binding a map.
     */
    <K, V> MapBuilder<K, V> bindMap(TypeLiteral<K> keyType, TypeLiteral<V> valueType, String bindingName);

    /**
     * Starts a binding of a java.util.Map&lt;K, V&gt; distinguished by its keys and values type.
     * Map binding should continue using returned MapBuilder.
     * This is a type safe way of binding a map.
     */
    <K, V> MapBuilder<K, V> bindMap(TypeLiteral<K> keyType, TypeLiteral<V> valueType);

    //----------------------
    //   List<T> bindings
    //----------------------

    /**
     * Starts a binding of a java.util.List&lt;T&gt; distinguished by its values type and qualifier annotation.
     * List binding should continue using returned ListBuilder. This is somewhat equivalent of
     * using "bind(List.class, qualifier)", however returned ListBuilder provides extra
     * DI capabilities.
     */
    <T> ListBuilder<T> bindList(Class<T> valueType, Class<? extends Annotation> qualifier);

    /**
     * Starts a binding of a java.util.List&lt;T&gt; distinguished by its values type and binding name.
     * List binding should continue using returned ListBuilder. This is somewhat equivalent of
     * using "bind(List.class, bindingName)", however returned ListBuilder provides extra
     * DI capabilities.
     */
    <T> ListBuilder<T> bindList(Class<T> valueType, String bindingName);

    /**
     * Starts a binding of a java.util.List&lt;T&gt; distinguished by its values type.
     * List binding should continue using returned ListBuilder.
     * This is somewhat equivalent of using "bind(List.class, bindingName)",
     * however returned ListBuilder provides extra DI capabilities.
     */
    <T> ListBuilder<T> bindList(Class<T> valueType);

    /**
     * Starts a binding of a java.util.List&lt;T&gt; distinguished by its values type and qualifier annotation.
     * List binding should continue using returned ListBuilder. This is somewhat equivalent of
     * using "bind(List.class, qualifier)", however returned ListBuilder provides extra
     * DI capabilities.
     */
    <T> ListBuilder<T> bindList(TypeLiteral<T> valueType, Class<? extends Annotation> qualifier);

    /**
     * Starts a binding of a java.util.List&lt;T&gt; distinguished by its values type and binding name.
     * List binding should continue using returned ListBuilder. This is somewhat equivalent of
     * using "bind(List.class, bindingName)", however returned ListBuilder provides extra
     * DI capabilities.
     */
    <T> ListBuilder<T> bindList(TypeLiteral<T> valueType, String bindingName);

    /**
     * Starts a binding of a java.util.List&lt;T&gt; distinguished by its values type.
     * List binding should continue using returned ListBuilder.
     * This is somewhat equivalent of using "bind(List.class, bindingName)",
     * however returned ListBuilder provides extra DI capabilities.
     */
    <T> ListBuilder<T> bindList(TypeLiteral<T> valueType);

    //----------------------
    //   Set<T> bindings
    //----------------------

    /**
     * Starts a binding of a java.util.Set&lt;T&gt; distinguished by its values type and qualifier annotation.
     * Set binding should continue using returned SetBuilder. This is somewhat equivalent of
     * using "bind(Set.class, qualifier)", however returned SetBuilder provides type safety and extra
     * DI capabilities.
     */
    <T> SetBuilder<T> bindSet(Class<T> valueType, Class<? extends Annotation> qualifier);

    /**
     * Starts a binding of a java.util.Set&lt;T&gt; distinguished by its values type and binding name.
     * Set binding should continue using returned SetBuilder. This is somewhat equivalent of
     * using "bind(Set.class, bindingName)", however returned SetBuilder provides type safety and extra
     * DI capabilities.
     */
    <T> SetBuilder<T> bindSet(Class<T> valueType, String bindingName);

    /**
     * Starts a binding of a java.util.Set&lt;T&gt; distinguished by its values type.
     * Set binding should continue using returned SetBuilder.
     * This is somewhat equivalent of using "bind(Set.class, bindingName)",
     * however returned SetBuilder provides type safety and extra DI capabilities.
     */
    <T> SetBuilder<T> bindSet(Class<T> valueType);

    /**
     * Starts a binding of a java.util.Set&lt;T&gt; distinguished by its values type and qualifier annotation.
     * Set binding should continue using returned SetBuilder. This is somewhat equivalent of
     * using "bind(Set.class, qualifier)", however returned SetBuilder provides type safety and extra
     * DI capabilities.
     */
    <T> SetBuilder<T> bindSet(TypeLiteral<T> valueType, Class<? extends Annotation> qualifier);

    /**
     * Starts a binding of a java.util.Set&lt;T&gt; distinguished by its values type and binding name.
     * Set binding should continue using returned SetBuilder. This is somewhat equivalent of
     * using "bind(Set.class, bindingName)", however returned SetBuilder provides type safety and extra
     * DI capabilities.
     */
    <T> SetBuilder<T> bindSet(TypeLiteral<T> valueType, String bindingName);

    /**
     * Starts a binding of a java.util.Set&lt;T&gt; distinguished by its values type.
     * Set binding should continue using returned SetBuilder.
     * This is somewhat equivalent of using "bind(Set.class, bindingName)",
     * however returned SetBuilder provides type safety and extra DI capabilities.
     */
    <T> SetBuilder<T> bindSet(TypeLiteral<T> valueType);

    //----------------------
    //     Decorations
    //----------------------

    /**
     */
    <T> DecoratorBuilder<T> decorate(Class<T> interfaceType);

    /**
     */
    <T> DecoratorBuilder<T> decorate(Key<T> key);
}
