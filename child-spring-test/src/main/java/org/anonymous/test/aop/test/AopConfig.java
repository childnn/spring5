package org.anonymous.test.aop.test;

import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.interceptor.DebugInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.util.ClassUtils;

import java.beans.Introspector;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2021/4/18 11:45
 */
@EnableAspectJAutoProxy
@Configuration
public class AopConfig {

	@Autowired
	private Person person;

	/**
	 * @see AnnotationBeanNameGenerator#generateBeanName
	 * This example involves:
	 *   1. A target bean that will be proxied. This is the 'person' bean definition in the example below.
	 *   2. An Advisor and an Interceptor used to provide advice.
	 *   3. An AOP proxy bean definition specifying the target object(the person bean) and the interfaces to proxy,
	 *   	along with the advices to apply.
	 * Note that the {@code interceptorNames} property takes a list of String: the bean names of the interceptor
	 * or advisors in the current factory. Advisors, interceptors, before, after returning and throws advice objects
	 * can be used. The ordering of advisors is significant.
	 * You might be wondering why the list doesn't hold bean references. The reason for this that if the
	 * {@link ProxyFactoryBean}'s singleton property is set to false, it must be able to return independent
	 * proxy instances. If any of the advisors is itself a prototype, an independent instance would need to be
	 * returned, so it's necessary to be able to obtain an instance of the protorype from the factory;
	 * holding a reference isn't sufficient.
	 * --
	 * Often we don't need the full power of the {@link ProxyFactoryBean}, because we're only interested
	 * in one aspect: For example, transaction management.
	 * 	{@link org.springframework.transaction.interceptor.TransactionProxyFactoryBean}
	 * There are a number of convenience factories we can use to create AOP proxies when we want to focus
	 * on a specific aspect.
	 *
	 */
	@Bean
	public ProxyFactoryBean personProxy() throws ClassNotFoundException {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		Class<?>[] classes = {Person.class};
		pfBean.setProxyInterfaces(classes);
		pfBean.setTarget(person);
		pfBean.setInterceptorNames(
				Introspector.decapitalize(ClassUtils.getShortName(MyAdvisor.class)),
				Introspector.decapitalize(ClassUtils.getShortName(DebugInterceptor.class)));

		return pfBean;
	}



}
