package org.anonymous.boot.test.config.mvc;

import org.anonymous.boot.test.annotation.Free;
import org.springframework.web.servlet.mvc.condition.CompositeRequestCondition;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/04/23
 */
public class MyRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

	@Override
	protected RequestCondition<?> getCustomMethodCondition(Method method) {
		// AnnotatedElementUtils.hasAnnotation()
		// 返回空表示不匹配
		// 如果标注了 Error 注解则返回空表示不符合要求
		// 否则, 如果直接走下面的 HeadersRequestCondition
		// 请求头不带 Test=true, 也会 404 -- 无法正常映射到 /error
		// 因为所有条件是且的关系
		if (method.isAnnotationPresent(Free.class) ||
				method.getDeclaringClass().isAnnotationPresent(Free.class)) {
			return null;
		}


		// return new HeadersRequestCondition("Test=true");
		// CompositeRequestCondition 中指定多个条件, 各个条件是 且的关系而不是或的关系
		return new CompositeRequestCondition(new HeadersRequestCondition("Test=true"));
	}
}
