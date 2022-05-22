package org.anonymous.boot.test.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @see org.springframework.web.util.ContentCachingRequestWrapper
 * @since 2022/04/23
 */
public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {
	/**
	 * Constructs a request object wrapping the given request.
	 *
	 * @param request the {@link javax.servlet.http.HttpServletRequest} to be wrapped.
	 * @throws IllegalArgumentException if the request is null
	 */
	public HttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

}
