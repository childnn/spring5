package org.anonymous.boot.test.config.aop;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @see org.springframework.aop.framework.adapter.MethodBeforeAdviceInterceptor
 * @since 2022/05/20
 */
public class ChildBeforeAdvice implements MethodBeforeAdvice {


	@Override
	public void before(Method method, Object[] args, Object target) throws Throwable {
		System.out.println("method = " + method);
		System.out.println("args = " + Arrays.toString(args));
		System.out.println("target = " + target);
	}


}
