package org.anonymous.boot.test.listener;

import org.anonymous.boot.test.event.BlockedListEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/03
 */
@Component
public class OrderingListener {

	// If you need one listener to be invoked before another one,
	// you can add the @Order annotation to the method declaration, as the following example shows:

	@EventListener
	@Order(42)
	public void processBlockedListEvent(BlockedListEvent event) {
		// notify appropriate parties via notificationAddress...
	}

}
