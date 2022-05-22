package org.anonymous.boot.test.listener;

import org.anonymous.boot.test.event.BlockedListEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @see org.springframework.context.support.ApplicationListenerDetector
 * @since 2022/05/03
 * To receive the custom ApplicationEvent,
 * you can create a class that implements ApplicationListener and register it as a Spring bean.
 */
@Component // 将 listener 注册为 spring-bean, 则会
public class AnnotationBlockedListNotifier {

	private String notificationAddress;

	public void setNotificationAddress(String notificationAddress) {
		this.notificationAddress = notificationAddress;
	}

	@EventListener/*(BlockedListEvent.class)*/
	public void processBlockedListEvent(BlockedListEvent event) {
		// notify appropriate parties via notificationAddress...
	}

	// If your method should listen to several events or if you want to define
	// it with no parameter at all, the event types can also be specified on the annotation itself.
	// The following example shows how to do so:
	@EventListener({ContextStartedEvent.class, ContextRefreshedEvent.class})
	public void handleContextStart() {
		// ...
	}

	// t is also possible to add additional runtime filtering by using the condition
	// attribute of the annotation that defines a SpEL expression,
	// which should match to actually invoke the method for a particular event.
	// The following example shows how our notifier can be rewritten to be
	// invoked only if the content attribute of the event is equal to my-event:

	/**
	 * @see org.springframework.context.event.EventListener#condition()
	 */
	@EventListener(condition = "#blEvent.content == 'my-event'")
	public void processBlockedListEventSpel(BlockedListEvent blEvent) {
		// notify appropriate parties via notificationAddress...
	}

	// If you need to publish an event as the result of processing another event,
	// you can change the method signature to return the event that should be published,
	// as the following example shows:
	// 1. This feature is not supported for asynchronous listeners. --- 异步监听不支持
	// 2. If you need to publish several events, you can return a Collection or an array of events instead.

	/**
	 * @see org.springframework.context.event.EventListenerMethodProcessor#afterSingletonsInstantiated() 处理标注 @EventListener 的方法
	 * @see org.springframework.context.event.EventListenerFactory#createApplicationListener 构建 ApplicationListenerMethodAdapter
	 * @see org.springframework.context.event.ApplicationListenerMethodAdapter#processEvent 处理消息 -- 又返回值也可以支持消息结果的转发
	 */
	@EventListener
	public BlockedListEvent handleBlockedListEvent(BlockedListEvent event) {
		// notify appropriate parties via notificationAddress and
		// then publish a BlockedListEvent...

		return null;
	}

}