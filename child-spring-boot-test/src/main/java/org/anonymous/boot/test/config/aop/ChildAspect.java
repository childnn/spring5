package org.anonymous.boot.test.config.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/15
 */
@Aspect("perthis(com.xyz.myapp.CommonPointcuts.businessService())")
public class ChildAspect {

	// Aspect Instantiation Models

	// This is an advanced topic. If you are just starting out with AOP, you can safely skip it until later.
	// y default, there is a single instance of each aspect within the application context.
	// AspectJ calls this the singleton instantiation model. It is possible to define aspects with alternate lifecycles.
	// Spring supports AspectJ’s perthis and pertarget instantiation models;
	// percflow, percflowbelow, and pertypewithin are not currently supported.
	//
	// You can declare a perthis aspect by specifying a perthis clause in the @Aspect annotation.
	// Consider the following example

	private int someState;

	@Before("com.xyz.myapp.CommonPointcuts.businessService()")
	public void recordServiceUsage() {
		// ...
	}

	// In the preceding example, the effect of the perthis clause is that one aspect instance is
	// created for each unique service object that performs a business service
	// (each unique object bound to this at join points matched by the pointcut expression).
	// The aspect instance is created the first time that a method is invoked on the service object.
	// The aspect goes out of scope when the service object goes out of scope.
	// Before the aspect instance is created, none of the advice within it runs.
	// As soon as the aspect instance has been created, the advice declared within it runs at matched join points,
	// but only when the service object is the one with which this aspect is associated.
	// See the AspectJ Programming Guide for more information on per clauses.
	//
	// The pertarget instantiation model works in exactly the same way as perthis,
	// but it creates one aspect instance for each unique target object at matched join points.

}
