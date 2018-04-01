package io.bootique.di;

/**
 *
 */
public interface DecoratorBuilder<T> {

    DecoratorBuilder<T> after(Class<? extends T> decoratorImplementationType) throws DIRuntimeException;

    DecoratorBuilder<T> before(Class<? extends T> decoratorImplementationType) throws DIRuntimeException;
}
