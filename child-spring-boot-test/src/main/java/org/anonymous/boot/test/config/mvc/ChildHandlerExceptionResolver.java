package org.anonymous.boot.test.config.mvc;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/24
 * If an exception occurs during request mapping or is thrown from a request handler (such as a @Controller),
 * the DispatcherServlet delegates to a chain of HandlerExceptionResolver beans to resolve the exception and provide alternative handling,
 * which is typically an error response.
 * --- 在 DispatcherServlet.properties 中配置
 * @see org.springframework.web.servlet.handler.SimpleMappingExceptionResolver
 *   A mapping between exception class names and error view names. Useful for rendering error pages in a browser application.
 * @see org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver
 * 	 Resolves exceptions raised by Spring MVC and maps them to HTTP status codes.
 * 	 See also alternative ResponseEntityExceptionHandler and REST API exceptions.
 * @see org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver
 *    Resolves exceptions with the @ResponseStatus annotation and maps them to HTTP status codes based on the value in the annotation.
 * @see org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver
 *     Resolves exceptions by invoking an @ExceptionHandler method in a @Controller or a @ControllerAdvice class. See @ExceptionHandler methods.
 *
 */
public class ChildHandlerExceptionResolver {

	/*
	Chain of Resolvers
		You can form an exception resolver chain by declaring multiple HandlerExceptionResolver beans in your
		Spring configuration and setting their order properties as needed.
		The higher the order property, the later the exception resolver is positioned.

		The contract of HandlerExceptionResolver specifies that it can return:
			- a ModelAndView that points to an error view.
			- An empty ModelAndView if the exception was handled within the resolver.
			- null if the exception remains unresolved, for subsequent resolvers to try, and,
				if the exception remains at the end, it is allowed to bubble up to the Servlet container.
      The MVC Config automatically declares built-in resolvers for default Spring MVC exceptions,
      for @ResponseStatus annotated exceptions, and for support of @ExceptionHandler methods.
      You can customize that list or replace it.

	 */
}
