package io.bootique.di.mock;

import javax.inject.Inject;
import java.util.List;

public class MockImplementation1_ListConfigurationMock5 implements MockInterface1 {

    private List<MockInterface5> configuration;

    @Inject
    public MockImplementation1_ListConfigurationMock5(List<MockInterface5> configuration) {
        this.configuration = configuration;
    }

    public String getName() {

        StringBuilder buffer = new StringBuilder();

        for (MockInterface5 value : configuration) {
            buffer.append(";").append(value);
        }

        return buffer.toString();
    }

}
