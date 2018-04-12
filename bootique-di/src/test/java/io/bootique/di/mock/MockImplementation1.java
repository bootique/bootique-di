
package io.bootique.di.mock;

import javax.inject.Singleton;

@Singleton
public class MockImplementation1 implements MockInterface1 {

	public String getName() {
		return "MyName";
	}

}
