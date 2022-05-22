package org.anonymous.boot.test.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.interceptor.DebugInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;

import java.lang.reflect.Method;

interface Pojo {
	void foo();

	void proxyFoo();

	void proxyBar();

}

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/17
 */
public class Aop {

	/**
	 * The key thing to understand here is that the client code inside the main(..) method of the Main class has a reference to the proxy.
	 * This means that method calls on that object reference are calls on the proxy.
	 * As a result, the proxy can delegate to all of the interceptors (advice) that are relevant to that particular method call.
	 * However, once the call has finally reached the target object (the SimplePojo reference in this case),
	 * any method calls that it may make on itself, such as this.bar() or this.foo(),
	 * are going to be invoked against the this reference, and not the proxy.
	 * This has important implications.
	 * It means that self-invocation is not going to result in the advice associated with a method invocation getting a chance to run.
	 * ------
	 * proxy.foo()  vs proxy.proxyFoo()
	 * ------
	 * Okay, so what is to be done about this? The best approach (the term "best" is used loosely here) is to refactor
	 * your code such that the self-invocation does not happen.
	 * This does entail some work on your part, but it is the best, least-invasive approach.
	 * The next approach is absolutely horrendous, and we hesitate to point it out, precisely because it is so horrendous.
	 * You can (painful as it is to us) totally tie the logic within your class to Spring AOP, as the following example shows:
	 * ------
	 * AspectJ 不是基于代理的 aop
	 * Finally, it must be noted that AspectJ does not have this self-invocation issue because it is not a proxy-based AOP framework.
	 *
	 * @see org.springframework.aop.aspectj.annotation.AspectJProxyFactory
	 * @see ProxyFactory
	 */
	@Test
	public void springAOP() {
		ProxyFactory pf = new ProxyFactory(new SimplePojo()); // target: SimplePojo

		pf.setExposeProxy(true);

		// You can add advices (with interceptors as a specialized kind of advice),
		// advisors, or both and manipulate them for the life of the ProxyFactory.
		// If you add an IntroductionInterceptionAroundAdvisor, you can cause the proxy to implement additional interfaces.
		//
		// There are also convenience methods on ProxyFactory (inherited from AdvisedSupport)
		// that let you add other advice types, such as before and throws advice.
		// AdvisedSupport is the superclass of both ProxyFactory and ProxyFactoryBean.

		pf.addAdvice(new RetryAdvice()); // myMethodInterceptor
		// pf.addAdvisor(new Advisor() {
		// 	@Override
		// 	public Advice getAdvice() {
		// 		return new RetryAdvice();
		// 	}
		//
		// 	@Override
		// 	public boolean isPerInstance() {
		// 		return false;
		// 	}
		// }); // myAdvisor

		Pojo proxy = (Pojo) pf.getProxy();

		// proxy.foo(); // this.bar()
		proxy.proxyFoo(); // proxy.proxyBar()

		Advised advised = (Advised) proxy;
		Advisor[] advisors = advised.getAdvisors();
		int oldAdvisorCount = advisors.length;
		System.out.println(oldAdvisorCount + " advisors");


		// Add an advice like an interceptor without a pointcut
		// Will match all proxied methods
		// Can use for interceptors, before, after returning or throws advice
		advised.addAdvice(new DebugInterceptor());

		// Add selective advice using a pointcut
		advised.addAdvisor(new DefaultPointcutAdvisor(new org.springframework.aop.Pointcut() {

			@Override
			public ClassFilter getClassFilter() {
				return cl -> cl.getName().startsWith("org.anonymous");
			}

			@Override
			public MethodMatcher getMethodMatcher() {
				return new MethodMatcher() {
					@Override
					public boolean matches(Method method, Class<?> targetClass) {
						return method.getName().startsWith("aop");
					}

					@Override
					public boolean isRuntime() {
						return false;
					}

					@Override
					public boolean matches(Method method, Class<?> targetClass, Object... args) {
						return false;
					}
				};
			}
		}/*mySpecialPointcut*/, new RetryAdvice()/*myAdvice*/));

		// assertEquals("Added two advisors", oldAdvisorCount + 2, advised.getAdvisors().length);

		// Advisor[] advisors = advised.getAdvisors();
		// int oldAdvisorCount = advisors.length;
		System.out.println(advised.getAdvisors().length + " advisors");
	}

	@Test
	public void aspectJAop() {
		// create a factory that can generate a proxy for the given target object
		AspectJProxyFactory factory = new AspectJProxyFactory(new SimplePojo());

		// add an aspect, the class must be an @AspectJ aspect
		// you can call this as many times as you need with different aspects
		// factory.addAspect(SecurityManager.class);
		factory.addAspect(MyAsp.class);

		// you can also add existing aspect instances, the type of the object supplied must be an @AspectJ aspect
		factory.addAspect(new YourAsp());

		// now get the proxy object...
		Pojo proxy = factory.getProxy();

		proxy.foo();
	}
}

@Aspect
class YourAsp {

}

@Aspect
class MyAsp {

	// The execution of any public method:
	@Pointcut("execution(public * foo(..)) || execution(public * bar(..))")
	private void publicMethod() {
	}

	@Around("publicMethod()")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		Object aThis = pjp.getThis();
		System.out.println("aThis = " + aThis);
		Object target = pjp.getTarget();
		System.out.println("target = " + target);
		Object result = pjp.proceed();
		System.out.println("result = " + result);
		return result;
	}


}

class RetryAdvice implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		System.out.println("before-----" + method);
		System.out.println("method = " + method);
		// invoke this, not the proxy
		Object result = invocation.proceed();
		System.out.println(method + "---=====after");
		return result;
	}

}

class SimplePojo implements Pojo {

	@Override
	public void foo() {
		// this next method invocation is a direct call on the 'this' reference
		System.out.println("foo");
		this.bar();
	}

	public void bar() {
		// some logic...
		System.out.println("bar");
	}

	/**
	 * This totally couples your code to Spring AOP, and it makes the class itself aware of
	 * the fact that it is being used in an AOP context, which flies in the face of AOP.
	 */
	@Override
	public void proxyFoo() {
		System.out.println("proxyFoo");
		// this works, but... gah!
		// exposeProxy = true
		((Pojo) AopContext.currentProxy()).proxyBar();
	}

	@Override
	public void proxyBar() {
		// some logic...
		System.out.println("proxyBar");
	}
}