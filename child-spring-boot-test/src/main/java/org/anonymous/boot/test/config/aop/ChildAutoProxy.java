package org.anonymous.boot.test.config.aop;

import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @see ChildAppConfig
 * @since 2022/05/21
 */
public class ChildAutoProxy {

	// Using the "auto-proxy" facility

	// So far, we have considered explicit creation of AOP proxies by using a ProxyFactoryBean or similar factory bean.
	//
	// Spring also lets us use “auto-proxy” bean definitions, which can automatically proxy selected bean definitions.
	// This is built on Spring’s “bean post processor” infrastructure, which enables modification of any bean definition as the container loads.
	//
	// In this model, you set up some special bean definitions in your XML bean definition file to configure the auto-proxy infrastructure.
	// This lets you declare the targets eligible for auto-proxying. You need not use ProxyFactoryBean.
	//
	// There are two ways to do this:
	//
	// - By using an auto-proxy creator that refers to specific beans in the current context.
	//
	// - A special case of auto-proxy creation that deserves to be considered separately:
	//   auto-proxy creation driven by source-level metadata attributes.

	// Auto-proxy Bean Definitions

	// The BeanNameAutoProxyCreator class is a BeanPostProcessor that automatically creates AOP proxies for beans with names
	// that match literal values or wildcards.
	// The following example shows how to create a BeanNameAutoProxyCreator bean:
	// As with auto-proxying in general, the main point of using BeanNameAutoProxyCreator is to apply the same configuration consistently to multiple objects,
	// with minimal volume of configuration. It is a popular choice for applying declarative transactions to multiple objects.
	// Bean definitions whose names match, such as jdkMyBean and onlyJdk in the preceding example, are plain old bean definitions with the target class.
	// An AOP proxy is automatically created by the BeanNameAutoProxyCreator. The same advice is applied to all matching beans.
	// Note that, if advisors are used (rather than the interceptor in the preceding example), the pointcuts may apply differently to different beans.
	@Bean
	BeanNameAutoProxyCreator beanNameAutoProxyCreator() {
		BeanNameAutoProxyCreator bean = new BeanNameAutoProxyCreator();
		// Set the names of the beans that should automatically get wrapped with proxies.
		bean.setBeanNames("jdk*", "onlyJdk");
		// As with ProxyFactoryBean, there is an interceptorNames property rather than a list of interceptors,
		// to allow correct behavior for prototype advisors.
		// Named “interceptors” can be advisors or any advice type.
		bean.setInterceptorNames("myInterceptor");

		return bean;
	}

	// A more general and extremely powerful auto-proxy creator is DefaultAdvisorAutoProxyCreator.
	// This automagically applies eligible advisors in the current context,
	// without the need to include specific bean names in the auto-proxy advisor’s bean definition.
	// It offers the same merit of consistent configuration and avoidance of duplication as BeanNameAutoProxyCreator.
	// Using this mechanism involves:
	//
	// - Specifying a DefaultAdvisorAutoProxyCreator bean definition.
	//
	// - Specifying any number of advisors in the same or related contexts.
	// 	  Note that these must be advisors, not interceptors or other advices.
	// 	  This is necessary, because there must be a pointcut to evaluate,
	// 	  to check the eligibility of each advice to candidate bean definitions.
	// The DefaultAdvisorAutoProxyCreator is very useful if you want to apply the same advice consistently to many business objects.
	// Once the infrastructure definitions are in place, you can add new business objects without including specific proxy configuration.
	// You can also easily drop in additional aspects (for example, tracing or performance monitoring aspects) with minimal change to configuration.
	//
	// The DefaultAdvisorAutoProxyCreator offers support for filtering (by using a naming convention so that only certain advisors are evaluated,
	// which allows the use of multiple, differently configured, AdvisorAutoProxyCreators in the same factory) and ordering.
	// Advisors can implement the org.springframework.core.Ordered interface to ensure correct ordering if this is an issue.
	// The TransactionAttributeSourceAdvisor used in the preceding example has a configurable order value.
	// The default setting is unordered.
	@Bean
	DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator bean = new DefaultAdvisorAutoProxyCreator();
		bean.setInterceptorNames("LockMixinAdvisor");
		// ...
		// bean.

		return bean;
	}

	@Bean
	TransactionAttributeSourceAdvisor transactionAttributeSourceAdvisor(ChildTxProxyConfig config) {
		TransactionAttributeSourceAdvisor bean =
				new TransactionAttributeSourceAdvisor(new TransactionInterceptor(
						config.transactionManager(), new AnnotationTransactionAttributeSource()));
		// bean.setTransactionInterceptor();
		return bean;
	}

}
