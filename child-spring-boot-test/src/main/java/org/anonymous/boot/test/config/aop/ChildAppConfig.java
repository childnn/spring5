package org.anonymous.boot.test.config.aop;

import org.anonymous.boot.test.service.LockableService;
import org.springframework.aop.framework.AbstractSingletonProxyFactoryBean;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.framework.adapter.MethodBeforeAdviceInterceptor;
import org.springframework.aop.interceptor.DebugInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @see org.springframework.aop.framework.ProxyFactoryBean
 * @see org.springframework.aop.framework.ProxyConfig
 * @see org.springframework.aop.framework.ProxyFactoryBean#GLOBAL_SUFFIX
 * @since 2022/05/09
 * <aop:aspectj-autoproxy/>
 */
@Configuration
@EnableAspectJAutoProxy
public class ChildAppConfig {


	/*
	In common with most FactoryBean implementations provided with Spring, the ProxyFactoryBean class is itself a JavaBean. Its properties are used to:
		- Specify the target you want to proxy.
		- Specify whether to use CGLIB
	Some key properties are inherited from org.springframework.aop.framework.ProxyConfig (the superclass for all AOP proxy factories in Spring).
	   These key properties include the following:
		- proxyTargetClass: true if the target class is to be proxied, rather than the target class’s interfaces.
			If this property value is set to true, then CGLIB proxies are created
        - optimize: Controls whether or not aggressive optimizations are applied to proxies created through CGLIB.
        	 You should not blithely use this setting unless you fully understand how the relevant AOP proxy handles optimization.
        	 This is currently used only for CGLIB proxies. It has no effect with JDK dynamic proxies.
        - frozen: If a proxy configuration is frozen, changes to the configuration are no longer allowed. This is useful both as a slight
        	optimization and for those cases when you do not want callers to be able to manipulate the proxy (through the Advised interface)
        	after the proxy has been created. The default value of this property is false, so changes (such as adding additional advice) are allowed.
        - exposeProxy: Determines whether or not the current proxy should be exposed in a ThreadLocal so that it can be accessed by the target.
        	If a target needs to obtain the proxy and the exposeProxy property is set to true, the target can use the AopContext.currentProxy() method.
      Other properties specific to ProxyFactoryBean include the following:
        - proxyInterfaces: An array of String interface names. If this is not supplied, a CGLIB proxy for the target class is used
		- interceptorNames: A String array of Advisor, interceptor, or other advice names to apply. Ordering is significant,
		  on a first come-first served basis. That is to say that the first interceptor in the list is the first to be able to intercept the invocation.
		  The names are bean names in the current factory, including bean names from ancestor factories.
		  You cannot mention bean references here, since doing so results in the ProxyFactoryBean ignoring the singleton setting of the advice.
		  You can append an interceptor name with an asterisk (*). Doing so results in the application of all advisor beans with names
		  that start with the part before the asterisk to be applied. You can find an example of using this feature in Using “Global” Advisors.
		- singleton: Whether or not the factory should return a single object, no matter how often the getObject() method is called.
	         Several FactoryBean implementations offer such a method. The default value is true.
	         If you want to use stateful advice - for example, for stateful mixins - use prototype advices along with a singleton value of false.

	 */

	/*
	  Using an anonymous inner bean has the advantage that there is only one object of type Person.
	  This is useful if we want to prevent users of the application context from obtaining a reference to
	  the un-advised object or need to avoid any ambiguity with Spring IoC autowiring.
	  There is also, arguably, an advantage in that the ProxyFactoryBean definition is self-contained.
	  However, there are times when being able to obtain the un-advised target from the factory might actually be an advantage (for example, in certain test scenarios).
	 */

	/**
	 * 1. 如果将 {@link LockableService} 显式的注册为 bean, 则 此处需要加 @Primary
	 * 因为当前 context 会有两个 {@link Lockable} bean
	 * 因为当前方法返回的也是一个 {@link Lockable} bean
	 * 2. 如果不希望 用户(其他业务代码) 使用未经代理的 {@link Lockable} bean,
	 * 则可以直接在此方法中 new LockableService(), 并设置为 #target
	 */
	// @Primary
	@Bean
	ProxyFactoryBean proxyFactoryBean(/*LockableService lockableService*/) {
		ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();

		// cglib
		proxyFactoryBean.setProxyTargetClass(true);
		// target
		proxyFactoryBean.setTarget(new LockableService());
		// proxyFactoryBean.setTargetClass(LockableService.class);
		// #setProxyInterfaces
		proxyFactoryBean.setInterfaces(Lockable.class);

		// advisor
		proxyFactoryBean.addAdvisor(new LockMixinAdvisor());

		/*
		 Note that the interceptorNames property takes a list of String, which holds the bean names of the interceptors or advisors in the current factory.
		 You can use advisors, interceptors, before, after returning, and throws advice objects.
		 The ordering of advisors is significant.

		 */

		/*
		  You might be wondering why the list does not hold bean references.
		  The reason for this is that, if the singleton property of the ProxyFactoryBean is set to false,
		  it must be able to return independent proxy instances.
		  If any of the advisors is itself a prototype, an independent instance would need to be returned,
		  so it is necessary to be able to obtain an instance of the prototype from the factory.
		  Holding a reference is not sufficient.
		 */
		proxyFactoryBean.setInterceptorNames("debugInterceptor", "methodBeforeAdviceInterceptor");

		//  Using “Global” Advisors
		// By appending an asterisk to an interceptor name, all advisors with bean names that match the part
		// before the asterisk are added to the advisor chain. This can come in handy if you need to add a standard set of “global” advisors.
		// @see org.springframework.aop.framework.ProxyFactoryBean#GLOBAL_SUFFIX
		// 自动识别以 global 开头的 bean-name
		proxyFactoryBean.setInterceptorNames("global*");

		return proxyFactoryBean;
	}

	@Bean
	DebugInterceptor debugInterceptor() {
		return new DebugInterceptor(true);
	}

	// @Bean
	AbstractSingletonProxyFactoryBean singletonProxyFactoryBean(LockableService lockableService) {
		AbstractSingletonProxyFactoryBean abstractSingletonProxyFactoryBean = new AbstractSingletonProxyFactoryBean() {

			@Override
			protected Object createMainInterceptor() {
				LockMixin lockMixin = new LockMixin(true);

				return new LockMixinAdvisor(lockMixin);
			}

		};

		abstractSingletonProxyFactoryBean.setTarget(lockableService);
		abstractSingletonProxyFactoryBean.setProxyTargetClass(true);

		return abstractSingletonProxyFactoryBean;
	}

	@Bean
	MethodBeforeAdviceInterceptor methodBeforeAdviceInterceptor() {
		return new MethodBeforeAdviceInterceptor(new ChildBeforeAdvice());
	}

}
