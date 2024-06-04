package org.anonymous.boot.test.servlet;

import org.anonymous.boot.test.BootApplication;
import org.anonymous.boot.test.config.servlet.ChildServlet3Config;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/22
 * @see javax.servlet.ServletContainerInitializer
 * @see org.springframework.web.SpringServletContainerInitializer
 * servlet-3.0+, servlet-container init META-INF/services/javax.servlet.ServletContainerInitializer
 * spring 实现类 {@link org.springframework.web.SpringServletContainerInitializer}
 * @see javax.servlet.annotation.HandlesTypes
 * @see org.springframework.web.WebApplicationInitializer
 * 使用者只需要实现 {@link org.springframework.web.WebApplicationInitializer}
 * @see org.apache.catalina.startup.WebappServiceLoader
 * ---
 * n addition to using the ServletContext API directly, you can also
 * extend {@link org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer} and override specific methods
 * For programmatic use cases, a {@link org.springframework.web.context.support.GenericWebApplicationContext}
 * 	can be used as an alternative to {@link org.springframework.web.context.support.AnnotationConfigWebApplicationContext}
 */
// @Component
public class ChildSpringServletContainerInitializer implements WebApplicationInitializer { // 基于 servlet-3.0

	// Spring Boot follows a different initialization sequence.
	// Rather than hooking into the lifecycle of the Servlet container, Spring Boot uses Spring configuration
	// to bootstrap itself and the embedded Servlet container. Filter and Servlet declarations are detected
	// in Spring configuration and registered with the Servlet container.

	// todo: 无法加载啊...? tomcat-9.0.60 --

	/**
	 * @see org.apache.catalina.Context#addServletContainerInitializer(javax.servlet.ServletContainerInitializer, java.util.Set)
	 * @param servletContext the {@code ServletContext} to initialize
	 */
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		System.out.println("初始化--servlet-3.0-ServletContainerInitializer.....servletContext = " + servletContext);
	}



	/*
	 WebApplicationInitializer is an interface provided by Spring MVC that ensures your implementation is detected and automatically
	 used to initialize any Servlet 3 container. An abstract base class implementation of WebApplicationInitializer named
	 AbstractDispatcherServletInitializer makes it even easier to register the DispatcherServlet by overriding methods to
	 specify the servlet mapping and the location of the DispatcherServlet configuration.
	 */

	/**
	 * @see org.springframework.web.servlet.support.RequestContextUtils
	 * @see org.springframework.web.context.support.WebApplicationContextUtils
	 */
	class ChildAbstractAnnotationConfigDispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

		// If an application context hierarchy is not required, applications can
		// return all configuration through getRootConfigClasses() and null from getServletConfigClasses().

		@Override
		protected Class<?>[] getRootConfigClasses() {
			return new Class[]{BootApplication.class};
		}

		@Override
		protected Class<?>[] getServletConfigClasses() {
			return new Class[] {ChildServlet3Config.class};
		}

		@Override
		protected String[] getServletMappings() {
			return new String[]{"/app1/*" };
		}

	}

	// If you use XML-based Spring configuration, you should extend directly from AbstractDispatcherServletInitializer,
	// as the following example shows:

	class MyWebAppInitializer extends AbstractDispatcherServletInitializer {

		@Override
		protected WebApplicationContext createRootApplicationContext() {
			return null;
		}

		@Override
		protected WebApplicationContext createServletApplicationContext() {
			XmlWebApplicationContext cxt = new XmlWebApplicationContext();
			cxt.setConfigLocation("/WEB-INF/spring/dispatcher-config.xml");
			return cxt;
		}

		@Override
		protected String[] getServletMappings() {
			return new String[]{"/"};
		}

		// AbstractDispatcherServletInitializer also provides a convenient way to add Filter instances and
		// have them be automatically mapped to the DispatcherServlet,
		// as the following example shows:
		// Each filter is added with a default name based on its concrete type and automatically mapped to the DispatcherServlet.
		@Override
		protected Filter[] getServletFilters() {
			return new Filter[]
					{
							new HiddenHttpMethodFilter(),
							new CharacterEncodingFilter()
					};
		}

		// The #isAsyncSupported protected method of AbstractDispatcherServletInitializer provides a single place
		// to enable async support on the DispatcherServlet and all filters mapped to it.
		// By default, this flag is set to true.
		//
		// Finally, if you need to further customize the DispatcherServlet itself, you can override the createDispatcherServlet method.

	}

}
