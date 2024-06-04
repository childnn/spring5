package org.anonymous.boot.test.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/03
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#registerCustomEditor(Class, Class)
 */
public class ChildApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		applicationContext.addBeanFactoryPostProcessor(new ChildPropertyEditorConfig());
	}

}
