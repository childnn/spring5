package org.anonymous.boot.test.config.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/04/24
 * @see org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration#setConfigurers(java.util.List)
 * @see org.springframework.web.context.support.ServletRequestHandledEvent
 * @see org.springframework.web.servlet.FrameworkServlet#publishRequestHandledEvent(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, long, Throwable)
 */
@Component
public class ChildWebMvcConfigurer implements WebMvcConfigurer {

	/**
	 * @param converters initially an empty list of converters
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport#getMessageConverters()
	 */
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		MappingJackson2HttpMessageConverter jsonResp = new MappingJackson2HttpMessageConverter(objectMapper);
		// jsonResp.setJsonPrefix("111");

		converters.add(0, jsonResp);
	}

	/**
	 * 处理器拦截器: 类似 filter
	 * 这里的 处理器 handler 就是指 {@link org.springframework.web.method.HandlerMethod}
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new ErrorHandlerInterceptor());
	}

	/**
	 * 处理器异常解析器
	 * @see ChildHandlerExceptionResolver
	 */
	@Override
	public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
		resolvers.add(new HandlerExceptionResolver() {
			@Override
			public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
												 Object handler, Exception ex) {
				System.out.println("HandlerExceptionResolver: " + ex);
				return null;
			}
		});
	}
}
