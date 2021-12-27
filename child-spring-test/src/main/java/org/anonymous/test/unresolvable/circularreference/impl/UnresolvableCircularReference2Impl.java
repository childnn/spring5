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
public class UnresolvableCircularReference2Impl implements UnresolvableCircularReference2 {

	private UnresolvableCircularReference1 cycleDependency1;

	public UnresolvableCircularReference2Impl(UnresolvableCircularReference1 cycleDependency1) {
		this.cycleDependency1 = cycleDependency1;
	}
}
