package org.anonymous.test.aop;

import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2021/4/16 11:12
 * @see org.springframework.aop.framework.adapter.AfterReturningAdviceAdapter
 */
public class MyAfterReturningAdvice implements AfterReturningAdvice {

	private int count;

	@Override
	public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {

	}

	public int getCount() {
		return count;
	}
}
