package org.anonymous.boot.test.config.mvc;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/04/23
 */
@Primary
@Component
public class CustomDispatcherServlet extends DispatcherServlet {

	// 输出请求详情
	@Override
	public boolean isEnableLoggingRequestDetails() {
		return true;
	}

	@Override
	public void setEnableLoggingRequestDetails(boolean enable) {
		super.setEnableLoggingRequestDetails(true);
	}
}
