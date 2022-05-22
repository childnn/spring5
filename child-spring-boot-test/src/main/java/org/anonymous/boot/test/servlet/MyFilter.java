package org.anonymous.boot.test.servlet;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @see OncePerRequestFilter
 * @since 2022/04/23
 */
@WebFilter(urlPatterns = "/*")
public class MyFilter extends HttpFilter {

	@Override
	protected void doFilter(HttpServletRequest req, HttpServletResponse res,
							FilterChain chain) throws IOException, ServletException {
		// ServletRequest servletRequest = sre.getServletRequest();
		// HttpServletRequest request = (HttpServletRequest) servletRequest;

		// 没有 request body 时 ContentLength == -1
		// ContentCachingRequestWrapper request = new ContentCachingRequestWrapper(req, req.getContentLength());

		ServletInputStream inputStream = null;
		try {
			// inputStream = request.getInputStream();
			// ObjectMapper objectMapper = new ObjectMapper();
			// JsonNode jsonNode = objectMapper.readTree(inputStream);
			// System.out.println("jsonNode = " + jsonNode);

			chain.doFilter(req, res);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
