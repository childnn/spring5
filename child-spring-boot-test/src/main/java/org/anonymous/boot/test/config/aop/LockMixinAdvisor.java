package org.anonymous.boot.test.config.aop;

import org.springframework.aop.support.DefaultIntroductionAdvisor;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @see org.springframework.aop.Advisor advisor 就是对 advice 的封装
 * @see org.springframework.aop.support.DefaultPointcutAdvisor
 * @see org.springframework.aop.PointcutAdvisor
 * @see org.springframework.aop.framework.Advised
 * @since 2022/05/20
 */
public class LockMixinAdvisor extends DefaultIntroductionAdvisor {
	private static final long serialVersionUID = 9041197703422863928L;

	/*
	  We can apply this advisor very simply, because it requires no configuration.
	  (However, it is impossible to use an IntroductionInterceptor without an IntroductionAdvisor.)
	  As usual with introductions, the advisor must be per-instance, as it is stateful.
	  We need a different instance of LockMixinAdvisor, and hence LockMixin, for each advised object.
	  The advisor comprises part of the advised object’s state.

   We can apply this advisor programmatically by using the Advised.addAdvisor() method or (the recommended way) in XML configuration, as any other advisor.
   All proxy creation choices discussed below, including “auto proxy creators,” correctly handle introductions and stateful mixins.


	 */

	/*
	  The required introduction only needs to hold a distinct LockMixin instance and specify the introduced interfaces (in this case, only Lockable).
	  A more complex example might take a reference to the introduction interceptor (which would be defined as a prototype).
	  In this case, there is no configuration relevant for a LockMixin, so we create it by using new.
	 */
	public LockMixinAdvisor() {
		// prototype
		this(new LockMixin(true));
		// super(new LockMixin(true), Lockable.class);
		System.out.println("serialVersionUID = " + serialVersionUID);
	}

	public LockMixinAdvisor(LockMixin lockMixin) {
		// prototype
		super(lockMixin, Lockable.class);
	}

}
