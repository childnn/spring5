package org.anonymous.boot.test.event;

import org.springframework.context.ApplicationEvent;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/03
 */
public class BlockedListEvent extends ApplicationEvent {
	private static final long serialVersionUID = -411749866381893598L;

	private final String address;
	private final String content;

	public BlockedListEvent(Object source, String address, String content) {
		super(source);
		this.address = address;
		this.content = content;
	}

	public String getAddress() {
		return address;
	}

	public String getContent() {
		return content;
	}

}
