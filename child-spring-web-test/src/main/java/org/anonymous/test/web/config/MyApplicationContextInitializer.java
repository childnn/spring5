package org.anonymous.test.web.config;

import org.springframework.context.ApplicationContextInitializer;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2020/10/14 20:08
 */
public class MyApplicationContextInitializer implements ApplicationContextInitializer<MyXmlWebAppContext> {

	@Override
	public void initialize(MyXmlWebAppContext applicationContext) {
		System.out.println(getClass() + "applicationContext = " + applicationContext);
	}

}
