package org.anonymous.boot.test.config;

import org.anonymous.boot.test.model.Response;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/03
 */
@RestControllerAdvice("org.anonymous.boot.test")
public class ChildExceptionHandler {

	@ExceptionHandler(ConstraintViolationException.class)
	Response<String> cve(ConstraintViolationException e) {
		String returnMsg = e.getConstraintViolations().stream()
				.map((cv) -> {
					String[] paths = Objects.toString(cv.getPropertyPath(), "").split("\\.");
					return paths.length == 0 ? cv.getMessage() : cv.getMessage() + "(" + paths[paths.length - 1] + ")";
				}).distinct().collect(Collectors.joining(","));
		Response<String> resp = new Response<>();
		resp.setMsg(returnMsg);
		resp.setContent(e.getClass().getName());
		return resp;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	Response<String> manve(MethodArgumentNotValidException e) {
		String returnMsg = e.getBindingResult().getFieldErrors().stream()
				.map((x) -> x.getDefaultMessage() + "(" + x.getField() + ")")
				.distinct().collect(Collectors.joining(","));
		Response<String> resp = new Response<>();
		resp.setMsg(returnMsg);
		resp.setContent(e.getClass().getName());
		return resp;
	}

	@ExceptionHandler({MissingServletRequestParameterException.class})
	Response<String> msrpe(MissingServletRequestParameterException e, HttpServletRequest request) {
		Response<String> resp = new Response<>();
		resp.setMsg(String.format("请求数据有误: [%s]不可为空", e.getParameterName()));
		resp.setContent(e.getClass().getName());
		return resp;
	}

	@ExceptionHandler({HttpMessageNotReadableException.class})
	Response<String> hmnre(HttpMessageNotReadableException e, HttpServletRequest request) {
		Response<String> resp = new Response<>();
		resp.setMsg("提交数据有误,请确认数据格式/类型是否正确");
		resp.setContent(e.getClass().getName());
		return resp;
	}

}
