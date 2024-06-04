package org.anonymous.boot.test.config.base;

import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.BootstrapRegistryInitializer;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/07/08
 */
public class ChildBootstrapRegistryInitializer implements BootstrapRegistryInitializer {

	@Override
	public void initialize(BootstrapRegistry registry) {
		System.out.println("ChildBootstrapRegistryInitializer--: " + registry.getClass().getName());
	}

}
