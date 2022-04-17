package org.anonymous.boot.test.action;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/04/16
 */
@RestController
public class HelloAction {

	@GetMapping
	String hello() {
		return "Hello spring boot";
	}

}
