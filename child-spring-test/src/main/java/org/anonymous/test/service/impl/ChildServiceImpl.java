package org.anonymous.test.service.impl;

import org.anonymous.test.service.BService;
import org.anonymous.test.service.ChildService;
import org.anonymous.test.sterotype.MyService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2020/1/17 15:41
 * 先执行 @PostConstruct/@PreDestroy, 后执行 InitializingBean/DisposableBean
 */
// @Service
@MyService
public class ChildServiceImpl implements ChildService,
		InitializingBean, DisposableBean {

	// @Autowired
	// @Qualifier("BServiceImpl")
	private final BService bService;

	private ChildServiceImpl(BService bService) {
		System.out.println("----------------------------------");
		this.bService = bService;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("InitializingBean-afterPropertiesSet");
	}

	@PostConstruct
	public void init() {
		System.out.println("@PostConstruct-init");
	}

	@Transactional //
	@Override
	public void hello() {
		System.out.println("hello spring!!!");
	}

	@PreDestroy
	public void gc() {
		System.out.println("@PreDestroy-gc");
	}


	@Override
	public void destroy() throws Exception {
		System.out.println("DisposableBean-destroy");
	}
}
