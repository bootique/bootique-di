
package io.bootique.di.spi;

import io.bootique.di.DIRuntimeException;
import io.bootique.di.DecoratorBuilder;
import io.bootique.di.Key;

/**
 * @since 4.0
 */
class DefaultDecoratorBuilder<T> implements DecoratorBuilder<T> {

    private Key<T> bindingKey;
    private DefaultInjector injector;

    DefaultDecoratorBuilder(Key<T> bindingKey, DefaultInjector injector) {
        this.bindingKey = bindingKey;
        this.injector = injector;
    }

    @Override
    public DecoratorBuilder<T> after(Class<? extends T> decoratorImplementationType) throws DIRuntimeException {
        injector.putDecorationAfter(bindingKey, decoratorProvider(decoratorImplementationType));
        return this;
    }

    @Override
    public DecoratorBuilder<T> before(Class<? extends T> decoratorImplementationType) throws DIRuntimeException {
        injector.putDecorationBefore(bindingKey, decoratorProvider(decoratorImplementationType));
        return this;
    }

    private DecoratorProvider<T> decoratorProvider(Class<? extends T> decoratorType) {
        DecoratorProvider<T> provider0 = new ConstructorInjectingDecoratorProvider<T>(decoratorType, injector);
        DecoratorProvider<T> provider1 = new FieldInjectingDecoratorProvider<T>(decoratorType, provider0, injector);
        return provider1;
    }

}
