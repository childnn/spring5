package org.anonymous.boot.test.listener;

import org.anonymous.boot.test.event.BlockedListEvent;
import org.springframework.context.ApplicationListener;
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
public class BlockedListNotifier implements ApplicationListener<BlockedListEvent> {

	private String notificationAddress;

	public void setNotificationAddress(String notificationAddress) {
		this.notificationAddress = notificationAddress;
	}

	@Override
	public void onApplicationEvent(BlockedListEvent event) {
		// notify appropriate parties via notificationAddress...
		String address = event.getAddress();
		String content = event.getContent();
		// ...
	}

}