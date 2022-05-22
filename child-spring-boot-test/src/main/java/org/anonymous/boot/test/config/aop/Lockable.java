package org.anonymous.boot.test.config.aop;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/20
 */
public interface Lockable {

	// Consider an example from the Spring test suite and suppose
	// we want to introduce the following interface to one or more objects:

	void lock();

	void unlock();

	boolean locked();

}
