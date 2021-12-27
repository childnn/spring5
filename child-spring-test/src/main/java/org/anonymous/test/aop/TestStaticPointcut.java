package org.anonymous.test.aop;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2021/4/15 10:27
 */
public class TestStaticPointcut extends StaticMethodMatcherPointcut {


	@Override
	public ClassFilter getClassFilter() {
		return ClassFilter.TRUE;
	}

	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		return false;
	}
}
