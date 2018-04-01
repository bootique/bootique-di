

package io.bootique.di.mock;

import java.util.List;

import io.bootique.di.Inject;

/**
 * @since 4.0
 */
public class MockImplementation1_ListConfigurationMock5 implements MockInterface1 {

    private List<MockInterface5> configuration;

    public MockImplementation1_ListConfigurationMock5(@Inject List<MockInterface5> configuration) {
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
