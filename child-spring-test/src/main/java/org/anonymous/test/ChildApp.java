package org.anonymous.test;

import org.anonymous.test.service.ChildService;
import org.anonymous.test.sterotype.MyService;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Arrays;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2020/1/17 15:37
 * @see org.springframework.context.annotation.ConfigurationClassUtils
 * full-Configuration-calss, lite-Configuration-class
 * @see org.springframework.context.annotation.ConfigurationClassParser
 * @see org.springframework.context.annotation.ComponentScanAnnotationParser
 * ---
 * spring-boot 配置文件设置
 * @see org.springframework.boot.context.config.ConfigFileApplicationListener
 * 替换配置文件名: 默认为 application.FileExtension
 * 可以 spring.config.name 修改自己的前缀
 * 默认 location: org.springframework.boot.context.config.ConfigFileApplicationListener#DEFAULT_SEARCH_LOCATIONS
 * 替换 location: spring.config.location
 * @see org.springframework.context.support.PostProcessorRegistrationDelegate
 * @see org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver
 * @see org.springframework.web.servlet.config.annotation.EnableWebMvc
 * @see BeanDefinitionParserDelegate#MULTI_VALUE_ATTRIBUTE_DELIMITERS
 */
@EnableTransactionManagement //
@ComponentScan(
		basePackageClasses = ChildApp.class,
		includeFilters = @ComponentScan.Filter(classes = MyService.class)
)
public class ChildApp {

	// 启动时在 settings 中设置 gradle-build and run using gradle, 使用 IDEA 模式会报错, 二者的编译目录不一样(out/build)
	// 不清楚具体原因

	/**
	 * 创建 bean 的策略, 默认 {@link org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#instantiationStrategy}
	 * @see org.springframework.beans.factory.support.CglibSubclassingInstantiationStrategy
	 * 填充 bean 属性 {@link org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#populateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, org.springframework.beans.BeanWrapper)}
	 */
	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(ChildApp.class);
		ctx.addBeanFactoryPostProcessor(bf -> {
			String[] beanDefinitionNames = bf.getBeanDefinitionNames();
			System.out.println("beanDefinitionNames = " + Arrays.asList(beanDefinitionNames));
		});

		ChildService bean = ctx.getBean(ChildService.class);
		bean.hello();

		ctx.close(); // destroy
	}



}
