package org.anonymous.test.aop;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2021/4/15 10:39
 * before advice, don't need to invoke proceed() method
 * @see org.springframework.aop.framework.adapter.MethodBeforeAdviceAdapter
 */
public class MyMethodBeforeAdvice implements MethodBeforeAdvice {

	private int count;

	/**
	 * Before advice can be used with any pointcut.
	 * cannot change the return value. if a before advice throws an exception, this will
	 * abort further execution of the interceptor chain.
	 * @param method method being invoked
	 * @param args arguments to the method
	 * @param target target of the method invocation. May be {@code null}.
	 * @throws Throwable if needed
	 */
	@Override
	public void before(Method method, Object[] args, Object target) throws Throwable {
		++count;
	}
}
