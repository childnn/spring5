package org.anonymous.boot.test.config.aop;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @see java.lang.reflect.InvocationHandler
 * @since 2022/05/20
 */
public class AopProxyClass {

	// What if you need to proxy a class, rather than one or more interfaces?

	/*
	 Imagine that in our earlier example, there was no Person interface.
	 We needed to advise a class called Person that did not implement any business interface.
	 In this case, you can configure Spring to use CGLIB proxying rather than dynamic proxies.
	 To do so, set the proxyTargetClass property on the ProxyFactoryBean shown earlier to true.
	 While it is best to program to interfaces rather than classes, the ability to advise classes that
	 do not implement interfaces can be useful when working with legacy code.
	 (In general, Spring is not prescriptive. While it makes it easy to apply good practices, it avoids forcing a particular approach.)

	 */

	// If you want to, you can force the use of CGLIB in any case, even if you do have interfaces.
	// CGLIB proxying works by generating a subclass of the target class at runtime.
	// Spring configures this generated subclass to delegate method calls to the original target.
	// 修饰者模式
	// The subclass is used to implement the Decorator pattern, weaving in the advice.

	/*
	 CGLIB proxying should generally be transparent to users. However, there are some issues to consider:
	  - Final methods cannot be advised, as they cannot be overridden.
      - There is no need to add CGLIB to your classpath. As of Spring 3.2, CGLIB is repackaged and included in the spring-core JAR.
         In other words, CGLIB-based AOP works “out of the box”, as do JDK dynamic proxies.
	 There is little performance difference between CGLIB proxying and dynamic proxies.
	   Performance should not be a decisive consideration in this case.

	 */


}
