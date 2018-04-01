

package io.bootique.di.mock;

import io.bootique.di.Inject;

import java.util.List;

public class MockImplementation2_ListConfiguration implements MockInterface2 {

    private List<Object> configuration;

    public MockImplementation2_ListConfiguration(@Inject("xyz") List<Object> configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getAlteredName() {
        return getName();
    }

    public String getName() {

        StringBuilder buffer = new StringBuilder();

        for (Object value : configuration) {
            buffer.append(";").append(value);
        }

        return buffer.toString();
    }
}
