package org.anonymous.boot.test.config.mvc;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/04/23
 * @see org.springframework.web.servlet.support.RequestContext#WEB_APPLICATION_CONTEXT_ATTRIBUTE
 * @see org.springframework.web.servlet.HandlerExceptionResolver
 */
@Configuration(proxyBeanMethods = false)
public class MyWebMvcConfig {

	/**
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport#createRequestMappingHandlerMapping()
	 */
	@Bean
	WebMvcRegistrations webMvcRegistrations() {
		return new WebMvcRegistrations() {
			@Override
			public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
				return new MyRequestMappingHandlerMapping();
			}
		};
	}

	// @Bean
	// InterceptorRegistry interceptorRegistry() {
	// 	return
	// }

	/**
	 * @see org.springframework.web.servlet.handler.AbstractHandlerMapping#detectMappedInterceptors(java.util.List)
	 * @see org.springframework.web.servlet.HandlerExecutionChain#getInterceptors()
	 */
	@Bean
	MappedInterceptor mappedInterceptor() {
		// 映射指定路径的 HandlerInterceptor
		return new MappedInterceptor(new String[]{"/app/*"}, new HandlerInterceptor() {
			@Override
			public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
									 Object handler) throws Exception {
				System.out.println("request = " + request);
				System.out.println("handler = " + handler);
				return true;
			}
		});
	}


	// 手动 注册 handler-method(s)
	// @Autowired // 1. Inject the target handler and the handler mapping for controllers.
	public void setHandlerMapping(RequestMappingHandlerMapping mapping, UserHandler handler)
			throws NoSuchMethodException {

		// 2. Prepare the request mapping meta data.
		RequestMappingInfo info = RequestMappingInfo
				.paths("/user/{id}").methods(RequestMethod.GET).build();

		// 3. Get the handler method.
		Method method = UserHandler.class.getMethod("getUser", Long.class);

		// 4. Add the registration.
		mapping.registerMapping(info, handler, method);
	}

	// @Controller
	class UserHandler {
		public Object getUser(Long id) {
			return null;
		}
	}

}
