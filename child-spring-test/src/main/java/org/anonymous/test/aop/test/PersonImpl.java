package org.anonymous.test.aop.test;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2021/4/18 11:44
 */
@Component
public class PersonImpl implements Person {

	public PersonImpl() {
		System.out.println(getClass());
	}

	public static void main(String[] args) {
		Map<Integer, Integer> collect = Stream.of(null, 1).collect(Collectors.toMap(Function.identity(), r -> 1));
		System.out.println("collect = " + collect);
	}
}
