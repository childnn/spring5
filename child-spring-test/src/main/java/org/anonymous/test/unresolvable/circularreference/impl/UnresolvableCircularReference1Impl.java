package org.anonymous.test.unresolvable.circularreference.impl;

import org.anonymous.test.unresolvable.circularreference.UnresolvableCircularReference1;
import org.anonymous.test.unresolvable.circularreference.UnresolvableCircularReference2;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2021/3/9 11:33
 */
// @Service
public class UnresolvableCircularReference1Impl implements UnresolvableCircularReference1 {

	private UnresolvableCircularReference2 cycleDependency2;

	/**
	 * Exception in thread "main" org.springframework.beans.factory.UnsatisfiedDependencyException:
	 * Error creating bean with name 'unresolvableCircularReference1Impl' defined in file
	 * [E:/dev-code/WorkSpace/sources/spring-framework-5.1.x/child-spring-test/build/classes/java/main/org/anonymous/test/unresolvable/circularreference/impl/UnresolvableCircularReference1Impl.class]:
	 * Unsatisfied dependency expressed through constructor parameter 0;
	 * nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException:
	 * Error creating bean with name 'unresolvableCircularReference2Impl' defined in file
	 * [E:/dev-code/WorkSpace/sources/spring-framework-5.1.x/child-spring-test/build/classes/java/main/org/anonymous/test/unresolvable/circularreference/impl/UnresolvableCircularReference2Impl.class]:
	 * Unsatisfied dependency expressed through constructor parameter 0;
	 * nested exception is org.springframework.beans.factory.BeanCurrentlyInCreationException:
	 * Error creating bean with name 'unresolvableCircularReference1Impl':
	 * Requested bean is currently in creation: Is there an unresolvable circular reference?
	 * ---
	 * Caused by: org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'unresolvableCircularReference1Impl':
	 * Requested bean is currently in creation: Is there an unresolvable circular reference?
	 */
	public UnresolvableCircularReference1Impl(UnresolvableCircularReference2 cycleDependency2) {
		this.cycleDependency2 = cycleDependency2;
	}

}
