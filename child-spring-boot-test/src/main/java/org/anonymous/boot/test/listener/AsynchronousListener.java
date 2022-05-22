package org.anonymous.boot.test.listener;

import org.anonymous.boot.test.event.BlockedListEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/03
 */
@Component
public class AsynchronousListener {

	// Be aware of the following limitations when using asynchronous events:
	// 1. If an asynchronous event listener throws an Exception,
	//    it is not propagated to the caller. See AsyncUncaughtExceptionHandler for more details.
	// 2. Asynchronous event listener methods cannot publish a subsequent event by returning a value.
	//    If you need to publish another event as the result of the processing,
	//    inject an ApplicationEventPublisher to publish the event manually.

	/**
	 * @see org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
	 * @see org.springframework.context.ApplicationEventPublisher
	 * @see org.springframework.context.ApplicationContext#publishEvent
	 */
	@EventListener
	@Async
	public void processBlockedListEvent(BlockedListEvent event) {
		// BlockedListEvent is processed in a separate thread
	}

}
