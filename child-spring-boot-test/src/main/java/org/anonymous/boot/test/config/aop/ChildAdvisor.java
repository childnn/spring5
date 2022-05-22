package org.anonymous.boot.test.config.aop;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @see org.springframework.aop.Advisor
 * @see org.springframework.aop.support.DefaultPointcutAdvisor
 * @since 2022/05/20
 */
public class ChildAdvisor {

	/*
	In Spring, an Advisor is an aspect that contains only a single advice object associated with a pointcut expression.

	Apart from the special case of introductions, any advisor can be used with any advice.
	org.springframework.aop.support.DefaultPointcutAdvisor
	is the most commonly used advisor class. It can be used with a MethodInterceptor, BeforeAdvice, or ThrowsAdvice.

	It is possible to mix advisor and advice types in Spring in the same AOP proxy.
	For example, you could use an interception around advice, throws advice, and before advice in one proxy configuration.
	Spring automatically creates the necessary interceptor chain.

	 */

}
