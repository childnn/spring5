package org.anonymous.test.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2021/3/9 18:10
 * todo: 1. 测试各种类型返回值: 如 Json, POJO 等
 */
@RestController
public class AController {

	@PostConstruct
	public void init() {
		System.out.println(this + "---init...");
	}

	/**
	 * http://localhost:8080/get(.do)
	 */
	@GetMapping("/get")
	public String get() {
		return "Hello world!";
	}

}
