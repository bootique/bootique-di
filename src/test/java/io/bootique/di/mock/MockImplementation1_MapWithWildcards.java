

package io.bootique.di.mock;

import java.util.Map;

import io.bootique.di.Inject;

/**
 * @since 4.0
 */
public class MockImplementation1_MapWithWildcards implements MockInterface1 {

    @Inject
    Map<String, Class<?>> testMap;

    @Override
    public String getName() {
        return "map:" + testMap.size();
    }
}
