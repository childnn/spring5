package org.anonymous.boot.test.config.aop;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @see org.springframework.aop.support.DelegatingIntroductionInterceptor#DelegatingIntroductionInterceptor()
 * init(this)
 * // We don't want to expose the control interface
 * suppressInterface(IntroductionInterceptor.class);
 * suppressInterface(DynamicIntroductionAdvice.class);
 * @since 2022/05/20
 */
public class LockMixin extends DelegatingIntroductionInterceptor implements Lockable {

	// 所谓 mixin, 就是把 IntroductionInterceptor 和 需要 处理的接口 Lockable 混合

	/*
	  This illustrates a mixin. We want to be able to cast advised objects to Lockable, whatever their type and call lock and unlock methods.
	  If we call the lock() method, we want all setter methods to throw a LockedException.
	  Thus, we can add an aspect that provides the ability to make objects immutable without them having any knowledge of it:
	  a good example of AOP.

	  We could implement IntroductionInterceptor directly, but using DelegatingIntroductionInterceptor is best for most cases.

	  The DelegatingIntroductionInterceptor is designed to delegate an introduction to an actual implementation of the introduced interfaces,
	  concealing the use of interception to do so. You can set the delegate to any object using a constructor argument.
	  The default delegate (when the no-argument constructor is used) is this.
	  Thus, in the next example, the delegate is the LockMixin subclass of DelegatingIntroductionInterceptor.
	  Given a delegate (by default, itself), a DelegatingIntroductionInterceptor instance looks for all interfaces
	  implemented by the delegate (other than IntroductionInterceptor) and supports introductions against any of them.

	  -- 手动指定 interfaces that should not be exposed
	  Subclasses such as LockMixin can call the suppressInterface(Class intf) method to suppress interfaces that should not be exposed.
	  However, no matter how many interfaces an IntroductionInterceptor is prepared to support,
	  the IntroductionAdvisor used controls which interfaces are actually exposed.
	  An introduced interface conceals any implementation of the same interface by the target.

	 Thus, LockMixin extends DelegatingIntroductionInterceptor and implements Lockable itself.
	 The superclass automatically picks up that Lockable can be supported for introduction,
	 so we do not need to specify that. We could introduce any number of interfaces in this way.

	  Note the use of the locked instance variable. This effectively adds additional state to that held in the target object.


	 */

	private boolean locked;

	public LockMixin() {
	}

	public LockMixin(boolean locked) {
		this.locked = locked;
	}

	static class LockedException extends RuntimeException {

		public LockedException() {

		}

		public LockedException(String msg) {
			super(msg);
		}

	}

	@Override
	public void lock() {
		this.locked = true;
	}

	@Override
	public void unlock() {
		this.locked = false;
	}


	/*
	    Often, you need not override the invoke() method. The DelegatingIntroductionInterceptor implementation
	    (which calls the delegate method if the method is introduced, otherwise proceeds towards the join point) usually suffices.
	    In the present case, we need to add a check: no setter method can be invoked if in locked mode.

	 */

	@Override
	public boolean locked() {
		return locked;
	}

	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		System.out.println("mi = " + mi);
		// 当方法名 #startsWith
		if (locked() && mi.getMethod().getName().indexOf("unlock") == 0) {
			throw new LockedException("无法解锁！");
		}
		return super.invoke(mi);
	}

}
