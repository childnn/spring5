package org.anonymous.boot.test.config.mvc;

import org.springframework.context.ApplicationListener;
import org.springframework.web.context.support.ServletRequestHandledEvent;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/24
 */
public class ChildServletRequestHandledEventListener implements ApplicationListener<ServletRequestHandledEvent> {
	@Override
	public void onApplicationEvent(ServletRequestHandledEvent event) {
		System.out.println("ServletRequestHandledEvent = " + event);
	}
}
