package org.anonymous.boot.test.config.servlet;

import org.anonymous.boot.test.servlet.ChildSpringServletContainerInitializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.TomcatServletWebServerFactoryCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.SpringServletContainerInitializer;

import java.util.Collections;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/22
 * @see TomcatServletWebServerFactory
 */
@Configuration
public class ChildServlet3Config {

	//-----------------------------------------------------------------------------------------------------
	// 加载 servlet-3.0- ServletContainerInitializer 或者 spring-WebApplicationInitializer 的几种方式
	//-----------------------------------------------------------------------------------------------------


	//-------------------------------------------------------------------------------------------------
	// 1. 通过 WebServerFactoryCustomizer 和 WebServerFactoryCustomizerBeanPostProcessor
	//-------------------------------------------------------------------------------------------------

	/**
	 * @see #tomcatContextCustomizer
	 */
	@Bean
	ChildWebServerFactoryCustomizer childWebServerFactoryCustomizer() {
		return new ChildWebServerFactoryCustomizer();
	}


	//-------------------------------------------------------------------------------------------------
	// 2. 直接将 TomcatContextCustomizer 注册为 bean, 且支持多个
	// 参见 org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryConfiguration.EmbeddedTomcat.tomcatServletWebServerFactory
	//-------------------------------------------------------------------------------------------------

	/**
	 * @see org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryConfiguration.EmbeddedTomcat#tomcatServletWebServerFactory(org.springframework.beans.factory.ObjectProvider, org.springframework.beans.factory.ObjectProvider, org.springframework.beans.factory.ObjectProvider)
	 */
	// @Bean
	TomcatContextCustomizer tomcatContextCustomizer() {
		return context -> context.addServletContainerInitializer(
				new SpringServletContainerInitializer(),
				Collections.singleton(ChildSpringServletContainerInitializer.class));
	}

	/**
	 * @see org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration#tomcatServletWebServerFactoryCustomizer
	 * @deprecated use {@link ChildWebServerFactoryCustomizer} instead.
	 */
	@Deprecated
	@Primary
	// @Bean // Consider renaming one of the beans or enabling overriding by setting spring.main.allow-bean-definition-overriding=true
	@ConditionalOnClass(name = "org.apache.catalina.startup.Tomcat")
	TomcatServletWebServerFactoryCustomizer tomcatServletWebServerFactoryCustomizer(ServerProperties serverProperties) {
		return new TomcatServletWebServerFactoryCustomizer(serverProperties) {
			@Override
			public void customize(TomcatServletWebServerFactory factory) {
				super.customize(factory);
				factory.addContextCustomizers(context -> context.addServletContainerInitializer(
						new SpringServletContainerInitializer(), Collections.singleton(ChildSpringServletContainerInitializer.class)));
			}
		};
	}

	/**
	 * @see org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory#configureContext
	 * @see org.springframework.boot.web.server.WebServerFactoryCustomizer#customize(org.springframework.boot.web.server.WebServerFactory)
	 * 通过 bean-post-processor 加载
	 * @see org.springframework.boot.web.server.WebServerFactoryCustomizerBeanPostProcessor#postProcessBeforeInitialization(Object, String)
	 */
	static class ChildWebServerFactoryCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
		@Override
		public void customize(TomcatServletWebServerFactory factory) {
			factory.addContextCustomizers(context -> context.addServletContainerInitializer(
					new SpringServletContainerInitializer(), Collections.singleton(ChildSpringServletContainerInitializer.class)));
		}
	}

}
