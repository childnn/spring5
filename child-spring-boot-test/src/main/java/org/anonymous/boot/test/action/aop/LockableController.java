package org.anonymous.boot.test.action.aop;

import org.anonymous.boot.test.annotation.Free;
import org.anonymous.boot.test.config.aop.Lockable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/20
 */
@Free
@RequestMapping("lock")
@RestController
public class LockableController {


	@Autowired
	private Lockable lockable;

	@PostConstruct
	void init() {
		Lockable lockable1 = lockable;
		System.out.println("lockable1 = " + lockable1);
	}


	@PostMapping("aop")
	String aop() {
		lockable.lock();
		return "";
	}

	/**
	 * @see org.anonymous.boot.test.config.aop.LockMixin
	 * 被拦截后失败
	 */
	@PostMapping("aopException")
	String aopException() {
		lockable.unlock();
		return "被拦截-失败";
	}

}
