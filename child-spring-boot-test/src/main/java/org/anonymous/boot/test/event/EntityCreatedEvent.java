package org.anonymous.boot.test.event;

import org.springframework.context.ApplicationEvent;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/03
 */
public class EntityCreatedEvent<T> extends ApplicationEvent {
	private static final long serialVersionUID = -7003852180062949335L;

	private final T content;

	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param source the object on which the event initially occurred (never {@code null})
	 */
	public EntityCreatedEvent(Object source, T content) {
		super(source);
		this.content = content;
	}

	public T getContent() {
		return content;
	}

}
