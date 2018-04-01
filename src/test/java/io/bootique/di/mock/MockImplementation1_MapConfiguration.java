
package io.bootique.di.mock;

import io.bootique.di.Inject;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MockImplementation1_MapConfiguration implements MockInterface1 {

    private Map<String, Object> configuration;

    @Inject
    public MockImplementation1_MapConfiguration(@Named("xyz") Map<String, Object> configuration) {
        this.configuration = configuration;
    }

    public String getName() {

        StringBuilder buffer = new StringBuilder();

        List<String> keys = new ArrayList<>(configuration.keySet());
        Collections.sort(keys);

        for (String key : keys) {
            buffer.append(";").append(key).append("=").append(configuration.get(key));
        }

        return buffer.toString();
    }
}
