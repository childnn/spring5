package org.anonymous.boot.test.servlet;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/22
 * @see ServletContextInitializer 必须注册为 bean 才可以被识别到, 与 {@link org.springframework.web.WebApplicationInitializer} 不同
 * 详见文档
 * ServletContextInitializer 属于 spring-boot
 * WebApplicationInitializer 属于 spring-framwork
 * @see org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext#createWebServer()
 * @see org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext#selfInitialize(javax.servlet.ServletContext)
 * --
 * @see ChildSpringServletContainerInitializer
 */
@Component
public class ChildSpringBootServletContextInitializer implements ServletContextInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		System.out.println("org.springframework.boot.web.servlet.ServletContextInitializer = " + servletContext);
	}

}
