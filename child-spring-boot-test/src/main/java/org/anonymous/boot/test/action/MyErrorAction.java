package org.anonymous.boot.test.action;

import org.anonymous.boot.test.annotation.Free;
import org.anonymous.boot.test.model.Request;
import org.anonymous.boot.test.model.Response;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @see org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver#afterPropertiesSet()
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor
 * @since 2022/04/23
 */
@Controller
public class MyErrorAction extends AbstractErrorController {

	private final ErrorAttributes errorAttributes;

	private final List<ErrorViewResolver> errorViewResolvers;

	public MyErrorAction(ErrorAttributes errorAttributes,
						 List<ErrorViewResolver> errorViewResolvers) {
		super(errorAttributes, errorViewResolvers);
		this.errorAttributes = errorAttributes;
		this.errorViewResolvers = errorViewResolvers;
	}

	/**
	 * @see org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController#error(javax.servlet.http.HttpServletRequest)
	 */
	@Free
	@ResponseBody
	// @RequestMapping("my-error")
	@RequestMapping("error")
	// 使用默认路径
	Response error(HttpServletRequest request) {
		// request.h
		HttpStatus status = getStatus(request);
		String requestURI = request.getRequestURI();
		Response<Request> response = new Response<>();
		response.setMsg(request.getRequestURL() + " " + status);
		Request r = new Request();
		r.setId("11");
		r.setName("jack");
		response.setContent(r);
		return response;
	}

}
