/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.web.servlet;

import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.UpgradeProtocol;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;
import java.util.stream.Collectors;

/**
 * Configuration classes for servlet web servers
 * <p>
 * Those should be {@code @Import} in a regular auto-configuration class to guarantee
 * their order of execution.
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Ivan Sopov
 * @author Brian Clozel
 * @author Stephane Nicoll
 * @author Raheela Asalm
 * @author Sergey Serdyuk
 */
@Configuration(proxyBeanMethods = false)
class ServletWebServerFactoryConfiguration {

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass({Servlet.class, Tomcat.class, UpgradeProtocol.class})
	@ConditionalOnMissingBean(value = ServletWebServerFactory.class, search = SearchStrategy.CURRENT)
	static class EmbeddedTomcat {

		@Bean
		TomcatServletWebServerFactory tomcatServletWebServerFactory(
				// ObjectProvider 表示有则注入, 没有不会报错
				// 并且 ObjectProvider 继承 Iterable, 可以注入多个
				ObjectProvider<TomcatConnectorCustomizer> connectorCustomizers,
				ObjectProvider<TomcatContextCustomizer> contextCustomizers,
				ObjectProvider<TomcatProtocolHandlerCustomizer<?>> protocolHandlerCustomizers) {
			TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
			factory.getTomcatConnectorCustomizers()
					.addAll(connectorCustomizers.orderedStream().collect(Collectors.toList()));
			factory.getTomcatContextCustomizers()
					.addAll(contextCustomizers.orderedStream().collect(Collectors.toList()));
			factory.getTomcatProtocolHandlerCustomizers()
					.addAll(protocolHandlerCustomizers.orderedStream().collect(Collectors.toList()));
			return factory;
		}

	}

	/**
	 * Nested configuration if Jetty is being used.
	 */
	// @Configuration(proxyBeanMethods = false)
	// @ConditionalOnClass({ Servlet.class, Server.class, Loader.class, WebAppContext.class })
	// @ConditionalOnMissingBean(value = ServletWebServerFactory.class, search = SearchStrategy.CURRENT)
	// static class EmbeddedJetty {
	//
	// 	@Bean
	// 	JettyServletWebServerFactory JettyServletWebServerFactory(
	// 			ObjectProvider<JettyServerCustomizer> serverCustomizers) {
	// 		JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
	// 		factory.getServerCustomizers().addAll(serverCustomizers.orderedStream().collect(Collectors.toList()));
	// 		return factory;
	// 	}
	//
	// }

	/**
	 * Nested configuration if Undertow is being used.
	 */
	// @Configuration(proxyBeanMethods = false)
	// @ConditionalOnClass({ Servlet.class, Undertow.class, SslClientAuthMode.class })
	// @ConditionalOnMissingBean(value = ServletWebServerFactory.class, search = SearchStrategy.CURRENT)
	// static class EmbeddedUndertow {
	//
	// 	@Bean
	// 	UndertowServletWebServerFactory undertowServletWebServerFactory(
	// 			ObjectProvider<UndertowDeploymentInfoCustomizer> deploymentInfoCustomizers,
	// 			ObjectProvider<UndertowBuilderCustomizer> builderCustomizers) {
	// 		UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
	// 		factory.getDeploymentInfoCustomizers()
	// 				.addAll(deploymentInfoCustomizers.orderedStream().collect(Collectors.toList()));
	// 		factory.getBuilderCustomizers().addAll(builderCustomizers.orderedStream().collect(Collectors.toList()));
	// 		return factory;
	// 	}
	//
	// 	@Bean
	// 	UndertowServletWebServerFactoryCustomizer undertowServletWebServerFactoryCustomizer(
	// 			ServerProperties serverProperties) {
	// 		return new UndertowServletWebServerFactoryCustomizer(serverProperties);
	// 	}
	//
	// }

}
