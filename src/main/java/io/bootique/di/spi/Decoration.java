
package io.bootique.di.spi;

import java.util.LinkedList;
import java.util.List;

class Decoration<T> {

    private List<DecoratorProvider<T>> decorators;

    Decoration() {
        this.decorators = new LinkedList<DecoratorProvider<T>>();
    }

    void before(DecoratorProvider<T> decoratorProvider) {
        decorators.add(0, decoratorProvider);
    }

    void after(DecoratorProvider<T> decoratorProvider) {
        decorators.add(decoratorProvider);
    }
    
    List<DecoratorProvider<T>> decorators() {
        return decorators;
    }

}
