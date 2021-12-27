package org.anonymous.test.service.impl;

import org.anonymous.test.service.BService;
import org.anonymous.test.service.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2021/3/9 11:22
 */
@Service
public class BServiceImpl implements BService {

	@Autowired
	private ChildService childService;

}
