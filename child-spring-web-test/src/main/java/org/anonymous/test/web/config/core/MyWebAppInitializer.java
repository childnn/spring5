package org.anonymous.test.web.config.core;

import org.anonymous.test.web.config.MyXmlWebAppContext;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2020/10/14 20:26
 */
public class MyWebAppInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext ctx) throws ServletException {
		XmlWebApplicationContext rootContext = new MyXmlWebAppContext();
		rootContext.setConfigLocation("classpath:app.xml");


		ServletRegistration.Dynamic dispatchServlet = ctx.addServlet("dispatchServlet", new DispatcherServlet(rootContext));
		dispatchServlet.setLoadOnStartup(1);
		dispatchServlet.addMapping("/");
		//dispatchServlet.setInitParameter("key", "value");
		//dispatchServlet.setInitParameters()
	}
}
