
package io.bootique.di.mock;

import java.util.List;

import io.bootique.di.Inject;

public class MockImplementation1_ListConfiguration implements MockInterface1 {

    private List<Object> configuration;

    public MockImplementation1_ListConfiguration(@Inject("xyz") List<Object> configuration) {
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
