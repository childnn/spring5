package org.anonymous.test;

import org.anonymous.test.service.ChildService;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2020/1/17 15:37
 */
@ComponentScan(basePackageClasses = ChildApp.class)
public class ChildApp {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(ChildApp.class);
		ChildService bean = ctx.getBean(ChildService.class);
		bean.hello();

		ctx.close(); // destroy
	}

}
