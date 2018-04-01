package io.bootique.di.mock;

import io.bootique.di.Inject;

import javax.inject.Named;
import java.util.List;

public class MockImplementation1_ListConfiguration implements MockInterface1 {

    private List<Object> configuration;

    @Inject
    public MockImplementation1_ListConfiguration(@Named("xyz") List<Object> configuration) {
        this.configuration = configuration;
    }

    public String getName() {

        StringBuilder buffer = new StringBuilder();

        for (Object value : configuration) {
            buffer.append(";").append(value);
        }

        return buffer.toString();
    }
}
