package org.anonymous.boot.test.service;

import org.anonymous.boot.test.event.BlockedListEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/03
 */
@Service
public class EmailService implements ApplicationEventPublisherAware {

	private List<String> blockedList;
	private ApplicationEventPublisher publisher;

	public void setBlockedList(List<String> blockedList) {
		this.blockedList = blockedList;
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	/**
	 * @see org.anonymous.boot.test.listener.BlockedListNotifier -- 消息监听
	 */
	public void sendEmail(String address, String content) {
		// blocked email address
		// 如果是被限制的 email 地址, 则发送消息, 做必要的处理
		if (blockedList.contains(address)) {
			publisher.publishEvent(new BlockedListEvent(this, address, content));
		} else {
			// send email
		}
	}

}
