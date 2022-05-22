package org.anonymous.boot.test.action;

import org.anonymous.boot.test.model.Request;
import org.anonymous.boot.test.model.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/04/16
 */
@RestController
public class HelloAction {

	/**
	 * @see RequestAttributes#SCOPE_REQUEST
	 * @see org.springframework.web.context.WebApplicationContext#SCOPE_REQUEST
	 */
	@GetMapping
	String hello() {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes instanceof ServletRequestAttributes) {
			ServletRequestAttributes requestAttributes1 = (ServletRequestAttributes) requestAttributes;
			HttpServletRequest request = requestAttributes1.getRequest();
			Enumeration<String> headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String name = headerNames.nextElement();
				System.out.println(name + ": " + request.getHeader(name));
			}
		}

		return "Hello spring boot";
	}

	/**
	 * @see org.springframework.web.servlet.mvc.condition.PatternsRequestCondition#getMatchingCondition(javax.servlet.http.HttpServletRequest)
	 * 路径不写表示匹配所有, / 表示具体的路径
	 */
	@PostMapping("/")
	Response body(@RequestBody Request request) {
		System.out.println("request = " + request);
		Response response = new Response();
		response.setMsg("body");
		return response;
	}

}
