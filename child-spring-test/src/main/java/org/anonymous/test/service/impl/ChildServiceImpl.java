package org.anonymous.test.service.impl;

import org.anonymous.test.service.ChildService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2020/1/17 15:41
 */
@Service
public class ChildServiceImpl implements ChildService {

	public ChildServiceImpl() {
		System.err.println("----------------------------------");
	}

	@PostConstruct
	public void init() {
		System.out.println("true = " + true);
	}

	@Override
	public void hello() {
		System.err.println("hello spring!!!");
	}

	@PreDestroy
	public void destroy() {
		System.err.println("destroy");
	}

}
