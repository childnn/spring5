package org.anonymous.boot.test.action;

import org.anonymous.boot.test.annotation.Free;
import org.anonymous.boot.test.model.Response;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/28
 */
@RestController
public class OptionalAction {


	@GetMapping("optional")
	Response<String> op(@RequestParam("id") Optional<String> idq) { // required = false
		return new Response<>("", idq.orElse("默认值"));
	}

	// When an @RequestParam annotation is declared as a Map<String, String> or MultiValueMap<String, String>,
	// without a parameter name specified in the annotation,
	// then the map is populated with the request parameter values for each given parameter name.
	@GetMapping("map")
	String map(@RequestParam Map<String, Object> params) {
		return params.entrySet()
				.stream()
				.map(e -> String.join("=", e.getKey(), e.getValue() + ""))
				.collect(Collectors.joining(";"));
	}


	// 1. If the target method parameter type is not String, type conversion is automatically applied. See Type Conversion.
	//
	// 2. When an @RequestHeader annotation is used on a Map<String, String>, MultiValueMap<String, String>, or HttpHeaders argument, the map is populated with all header values.
	// 3. Built-in support is available for converting a comma-separated string into an array or collection of strings or other types known to the type conversion system.
	// 		For example, a method parameter annotated with @RequestHeader("Accept") can be of type String but also String[] or List<String>.
	/**
	 * @see org.springframework.beans.BeanUtils#isSimpleProperty
	 */
	@Free
	@GetMapping("non")
	String non(String id, int i,
			   @RequestHeader(value = "Content-Type", required = false) String aaa,
			   @RequestHeader(value = "Keep-Alive", required = false) Long /*long*/ time,
			   @RequestHeader("Accept") List<String> accepts) {
		accepts.forEach(System.out::println);
		accepts.addAll(Arrays.asList(id, i + "", aaa, time + ""));
		return String.join(",", accepts);
	}

	@Free
	@GetMapping("/cookies")
	String cookies(@CookieValue("JSESSIONID") String cookie) {
		//...

		return cookie;
	}

}
