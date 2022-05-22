package org.anonymous.boot.test.service;

import org.anonymous.boot.test.config.aop.Lockable;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/20
 */
// @Service
public class LockableService implements Lockable {

	protected boolean lock;

	@Override
	public void lock() {

	}

	@Override
	public void unlock() {

	}

	@Override
	public boolean locked() {
		return false;
	}

	public void setLock(boolean lock) {
		this.lock = lock;
	}

}
