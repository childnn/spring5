package org.anonymous.boot.test.config.aop;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;

import java.util.Properties;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @see ChildAppConfig
 * @since 2022/05/21
 */
// @Configuration
public class ChildTxProxyConfig implements BeanDefinitionRegistryPostProcessor {

	private String url;
	private String username;
	private String pwd;

	@Bean
	DriverManagerDataSource dataSource() {
		DriverManagerDataSource bean = new DriverManagerDataSource(url, username, pwd);
		bean.setDriverClassName("");

		return bean;
	}

	@Bean
	PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource());
		transactionManager.setNestedTransactionAllowed(true);
		// todo: other properties
		return transactionManager;
	}

	// This is never instantiated itself, so it can actually be incomplete.
	// Then, each proxy that needs to be created is a child bean definition,
	// which wraps the target of the proxy as an inner bean definition,
	// since the target is never used on its own anyway.
	// @Bean // bean 如何注册为 abstract?
	TransactionProxyFactoryBean txProxyTemplate() {
		TransactionProxyFactoryBean bean = new TransactionProxyFactoryBean();
		bean.setTransactionManager(transactionManager());
		bean.setTransactionAttributes(new Properties() {{
			setProperty("*", "PROPAGATION_REQUIRED");
		}});
		// DefaultTransactionAttribute transactionAttribute = new DefaultTransactionAttribute(PROPAGATION_REQUIRED);

		return bean;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}

	/**
	 * 将 TransactionProxyFactoryBean 注册为 bean-definition, 其自己为 abstract, 不会被实例化, 而是作为其他 bean-definition 的 parent-definition
	 *
	 * @param registry the bean definition registry used by the application context
	 */
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		// Note that in the parent bean example, we explicitly marked the parent bean definition as being abstract
		// by setting the abstract attribute to true, as described previously, so that it may not actually ever be instantiated.
		AbstractBeanDefinition beanDefinition =
				BeanDefinitionBuilder.genericBeanDefinition(TransactionProxyFactoryBean.class)
						.setAbstract(true)
						.addPropertyValue("transactionManager", transactionManager())
						.addPropertyValue("transactionAttributes", new Properties() {{
							setProperty("*", "PROPAGATION_REQUIRED");
						}})
						.getBeanDefinition();
		registry.registerBeanDefinition("txProxyTemplate", beanDefinition);
	}

	// The following example shows such a child bean:

	// 参见: beans-aop.xml

}
