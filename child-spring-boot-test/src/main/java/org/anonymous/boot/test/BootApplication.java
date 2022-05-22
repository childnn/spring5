package org.anonymous.boot.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @see org.springframework.web.servlet.handler.AbstractHandlerMethodMapping#afterPropertiesSet()
 * todo: xml/java-config 同时存在时的加载顺序问题的验证
 * Annotation injection is performed before XML injection.
 * Thus, the XML configuration overrides the annotations for properties wired through both approaches.
 * @since 2022/04/16
 */
@ServletComponentScan("org.anonymous.boot.test.servlet")
@SpringBootApplication
public class BootApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootApplication.class, args);
	}

}
