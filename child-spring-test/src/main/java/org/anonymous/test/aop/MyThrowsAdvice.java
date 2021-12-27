package org.anonymous.test.aop;

import org.springframework.aop.ThrowsAdvice;
import org.springframework.aop.framework.adapter.ThrowsAdviceInterceptor;

import java.lang.reflect.Method;
import java.rmi.RemoteException;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2021/4/16 10:57
 * @see ThrowsAdvice interface dose not catain any methods: it is a tag interface
 * identifying that the given object implements one or more typed throws advice
 * methods. These should be of form
 * 		afterThrowing([Method], [args], [target], subclassOfThrowable)
 * Only the last argument is required. Thus there from one to four arguments,
 * depending on whether the advice method is interested in the method and arguments.
 * @see ThrowsAdviceInterceptor
 */
public class MyThrowsAdvice implements ThrowsAdvice {

	// example
	public void afterThrowing(RemoteException ex) throws Throwable {
		// Do something with remote exception
	}

	public void afterThrowing(Method m, Object[] args, Object target, IllegalArgumentException ex) {
		// do something with all arguments
	}

	// 可以定义多个重载方法

}
