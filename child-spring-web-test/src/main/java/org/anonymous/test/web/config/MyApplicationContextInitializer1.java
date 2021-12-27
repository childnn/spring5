package org.anonymous.test.web.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2020/10/14 20:08
 */
public class MyApplicationContextInitializer1 implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	@Override
	public void initialize(ConfigurableApplicationContext ctx) {
		// ctx.addBeanFactoryPostProcessor(); spring-boot 中存在手动添加后置 BeanFactory 处理器

	}
}
