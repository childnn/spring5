package org.anonymous.boot.test.config.mvc;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/24
 */
public class ErrorHandlerInterceptor implements HandlerInterceptor {

	/**
	 * @see org.springframework.web.method.support.InvocableHandlerMethod#invokeForRequest
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
								Object handler, Exception ex) throws Exception {
		if (ex != null) {
			String requestURI = request.getRequestURI();
			System.out.println("requestURI = " + requestURI);
			// request.get
		}
	}

}
