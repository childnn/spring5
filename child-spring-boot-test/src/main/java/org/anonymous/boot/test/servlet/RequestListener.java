package org.anonymous.boot.test.servlet;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/04/23
 */
// @Component
@WebListener
public class RequestListener implements ServletRequestListener, BeanFactoryAware, ApplicationContextAware {

	// 每一次请求都是一个新的 listener 对象, 无法注入 Spring-IoC
	private ConfigurableBeanFactory configurableBeanFactory;

	@Override
	public void requestInitialized(ServletRequestEvent sre) {
		ServletRequest servletRequest = sre.getServletRequest();
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		// ServletInputStream inputStream = null;
		// try {
		// 	inputStream = request.getInputStream();
		// 	ObjectMapper objectMapper = new ObjectMapper();
		// 	JsonNode jsonNode = objectMapper.readTree(inputStream);
		// 	System.out.println("jsonNode = " + jsonNode);
		// } catch (IOException e) {
		// 	throw new RuntimeException(e);
		// }


		RequestAttributes requestAttributes = new ServletRequestAttributes(request);
		// RequestContextHolder.currentRequestAttributes();
		requestAttributes.registerDestructionCallback("aaa", () -> {
			// ServletRequestAttributes requestAttributes1 = servletRequest1.getHeaderNames();
			// HttpServletRequest request = requestAttributes1.getRequest();
			Enumeration<String> headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String name = headerNames.nextElement();
				System.out.println(name + ": " + request.getHeader(name));
			}
		}, RequestAttributes.SCOPE_REQUEST);

		RequestContextHolder.setRequestAttributes(requestAttributes);

		// String[] registeredScopeNames = configurableBeanFactory.getRegisteredScopeNames();
		//
		// Scope requestScope = configurableBeanFactory.getRegisteredScope(WebApplicationContext.SCOPE_REQUEST);
		// requestScope.registerDestructionCallback("", () -> {
		// 	System.out.println("requestScope = " + requestScope);
		// });
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		System.out.println("beanFactory = " + beanFactory);
		if (beanFactory instanceof ConfigurableBeanFactory) {
			this.configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println("applicationContext = " + applicationContext);
	}
}
