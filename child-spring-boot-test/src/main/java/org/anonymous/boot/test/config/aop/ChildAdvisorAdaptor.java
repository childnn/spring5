package org.anonymous.boot.test.config.aop;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @see org.springframework.aop.framework.adapter.AdvisorAdapter
 * @see org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry#getInstance()
 * @see org.springframework.aop.framework.adapter.DefaultAdvisorAdapterRegistry#registerAdvisorAdapter(org.springframework.aop.framework.adapter.AdvisorAdapter)
 * @since 2022/05/21
 */
public class ChildAdvisorAdaptor {

	// Defining New Advice Types
	// Spring AOP is designed to be extensible. While the interception implementation strategy is presently used internally,
	// it is possible to support arbitrary advice types in addition to the interception around advice, before, throws advice, and after returning advice.
	//
	// The org.springframework.aop.framework.adapter package is an SPI package that lets support
	// for new custom advice types be added without changing the core framework.
	// The only constraint on a custom Advice type is that it must implement the org.aopalliance.aop.Advice marker interface.
	//
	// See the org.springframework.aop.framework.adapter javadoc for further information.

}
