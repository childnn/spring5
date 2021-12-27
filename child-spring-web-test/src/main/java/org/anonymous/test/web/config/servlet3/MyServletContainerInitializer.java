package org.anonymous.test.web.config.servlet3;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.util.Set;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2021/3/9 17:02
 * Java-based web container, 不依赖 web.xml
 * 将 {@link ServletContainerInitializer} 的实现类全限定名放在 /META-INF/services/javax.servlet.ServletContainerInitializer
 * 文件中, 文件名就是 javax.servlet.ServletContainerInitializer. web-container(如: tomcat) 会自动解析.
 * @see javax.servlet.annotation.HandlesTypes
 * todo: tomcat 9.0.37 加载不了此类, 不知为何. tomcat 8.5.57 可以
 * Spring 本身存在有 {@link org.springframework.web.SpringServletContainerInitializer}
 * 此时存在多个 ServletContainerInitializer, 都会执行.
 * 在使用 Spring-web 时, 实际上无需自行实现 ServletContainerInitializer,
 * 只需实现 {@link org.springframework.web.WebApplicationInitializer} 即可,
 * 在 {@link org.springframework.web.SpringServletContainerInitializer} 上标有注解
 * {@link javax.servlet.annotation.HandlesTypes} 注解, 会解析 {@link org.springframework.web.WebApplicationInitializer}
 * --
 * @see org.anonymous.test.web.config.core.MyWebAppInitializer
 */
@Deprecated // 测试用, 实际不会使用
@HandlesTypes({})
public class MyServletContainerInitializer implements ServletContainerInitializer {

	public MyServletContainerInitializer() {
		System.out.println(this + "----init...");
	}

	/**
	 * Web-container 启动方法
	 * @param c {@link javax.servlet.annotation.HandlesTypes#value()}
	 * @param ctx the current servlet context
	 */
	@Override
	public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {

		// registerContextParams(ctx);

		// registerListener(ctx);
	}

	private void registerContextParams(ServletContext ctx) {
		// spring-web-root context
		ctx.setInitParameter(ContextLoader.CONFIG_LOCATION_PARAM, "classpath:app.xml"); // ok
		// ctx.setInitParameter(ContextLoader.CONFIG_LOCATION_PARAM, "/WEB-INF/classes/app.xml"); // ok
	}

	private void registerListener(ServletContext ctx) {
		ctx.addListener(ContextLoaderListener.class);
	}

}
