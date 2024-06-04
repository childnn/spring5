package org.anonymous.boot.test.ioc;

import java.util.HashMap;
import java.util.Map;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/29
 */
public class IoC {

	private static final Map<String, Object> ioc = new HashMap<>();


	@SuppressWarnings("unchecked")
	public static <T> T getBean(String className) {
		return (T) ioc.computeIfAbsent(className, IoC::newInstance);
	}

	private static Object newInstance(String cn)  {
		try {
			return Class.forName(cn).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
