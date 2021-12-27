package org.anonymous.test.web.config.core;

import org.anonymous.test.web.controller.AController;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2020/10/14 20:26
 * @see org.springframework.web.SpringServletContainerInitializer
 * todo: 可以不需要 {@link org.springframework.web.context.ContextLoaderListener} 吗?
 * .. 本身 {@link javax.servlet.ServletContextListener} 多用来加载配置文件, 全局参数等作用, 因此不是必须的...
 * --
 * @see org.springframework.web.servlet.HttpServletBean#initServletBean() -- DispatcherServlet
 */
public class MyWebAppInitializer implements WebApplicationInitializer {

	public MyWebAppInitializer() {
		System.out.println(this + "---init...");
	}

	@Override
	public void onStartup(ServletContext ctx) throws ServletException {

		//---------------------------------
		// 根据需要添加这些参数
		//---------------------------------
		// addListeners(ctx);
		// addFilters(ctx);
		// initParams(ctx);
		// attrs(ctx);


		// WebApplicationContext and DispatcherServlet

		// xml-based:
		// XmlWebApplicationContext rootContext = new MyXmlWebAppContext();
		// rootContext.setConfigLocation("classpath:app.xml");

		// annotation-based
		final AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.setAllowBeanDefinitionOverriding(false); // 不允许覆盖同名 BD
		// CircularReference: false 意味着完全禁止循环依赖, 默认 true, Spring-IoC 会尝试解决
		// rootContext.setAllowCircularReferences();
		// rootContext.register(AController.class);
		rootContext.scan(AController.class.getPackage().getName());


		final DispatcherServlet dispatcherServlet = getDispatcherServlet(rootContext);
		ServletRegistration.Dynamic dispatchServlet = ctx.addServlet("myDispatcherServlet",
				dispatcherServlet);
		dispatchServlet.setLoadOnStartup(1);
		dispatchServlet.addMapping("*.do", "/");
		// dispatchServlet.setInitParameter("key", "value");
		// dispatchServlet.setInitParameters();
	}

	private DispatcherServlet getDispatcherServlet(AnnotationConfigWebApplicationContext rootContext) {
		final DispatcherServlet dispatcherServlet = new DispatcherServlet(rootContext);
		/**
		 * @see org.springframework.web.servlet.FrameworkServlet#logResult(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Throwable, org.springframework.web.context.request.async.WebAsyncManager)
		 */
		dispatcherServlet.setEnableLoggingRequestDetails(true); // request 详细日志信息
		/**
		 * @see org.springframework.web.servlet.DispatcherServlet#noHandlerFound(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
		 */
		dispatcherServlet.setThrowExceptionIfNoHandlerFound(true); // 请求没有对应的 handler 是否报错: 默认 false, 返回 404
		return dispatcherServlet;
	}

	/**
	 * key: String
	 * value: Object
	 * @see #initParams(javax.servlet.ServletContext)
	 */
	private void attrs(ServletContext ctx) {
		ctx.setAttribute("attrName", Object.class);
	}

	/**
	 * web.xml, <context-param>
	 * key: String
	 * value: String
	 * @see #attrs(javax.servlet.ServletContext)
	 */
	private void initParams(ServletContext ctx) {
		ctx.setInitParameter("paramKey", "paramValue");
	}

	private void addFilters(ServletContext ctx) {
		final CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter(
				StandardCharsets.UTF_8.name(), true, true);

		final FilterRegistration.Dynamic myEncodingFilter = ctx.addFilter("myEncodingFilter", characterEncodingFilter);
		// final FilterRegistration.Dynamic myEncodingFilter = ctx.addFilter("myEncodingFilter", CharacterEncodingFilter.class);
		myEncodingFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class),
				false, // todo: 这里是否表示在其他所有 Filter 之前执行?
				"/**");
		// myEncodingFilter.setInitParameter("forceRequestEncoding", "true");
		// myEncodingFilter.setInitParameter("forceResponseEncoding", "true");
		// myEncodingFilter.setInitParameter("encoding", StandardCharsets.UTF_8.name());

	}

	/**
	 * @see javax.servlet.ServletContextListener
	 */
	private void addListeners(ServletContext ctx) {
		// example:
		ctx.addListener(ContextLoaderListener.class);
	}

}
