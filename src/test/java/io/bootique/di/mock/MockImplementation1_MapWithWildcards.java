package io.bootique.di.mock;

import javax.inject.Inject;
import java.util.Map;

public class MockImplementation1_MapWithWildcards implements MockInterface1 {

    @Inject
    Map<String, Class<?>> testMap;

    @Override
    public String getName() {
        return "map:" + testMap.size();
    }
}
