package org.anonymous.test.web.config;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2021/3/10 9:56
 * Server-start 执行先后顺序:
 * @see org.springframework.web.SpringServletContainerInitializer 自动被 WebContainer 解析: Servlet 3.0+
 * @see org.springframework.web.servlet.HttpServletBean#init() init servelt: the DispatcherServlet
 * -- 以下都以最常见的注解形式的 WebMVC 为例
 * --- MappingRegistery
 * @see org.springframework.web.servlet.handler.AbstractHandlerMethodMapping#mappingRegistry
 * @see org.springframework.web.servlet.handler.AbstractHandlerMethodMapping#registerMapping(java.lang.Object, java.lang.Object, java.lang.reflect.Method)
 * ---
 * @see org.springframework.web.servlet.mvc.method.RequestMappingInfo 顾名思义
 * 所谓的 handler 就是 request 指向的那个方法
 * @see org.springframework.web.method.HandlerMethod 封装了 @RequestMapping 方法的信息
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
 * @see org.springframework.web.servlet.HandlerExecutionChain 封装 handler 和 HandlerInterceptor
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter 对与 handler 类型为 HandlerMethod 的适配器
 * ---
 * @see org.springframework.web.servlet.DispatcherServlet
 * 接受 Request 后执行先后顺序(Servlet 的方法链):  service -> doGet/doPost -> processRequest -> doService -> doDispatch
 * handler 相关: 在 {@link org.springframework.web.servlet.DispatcherServlet#doDispatch} 中执行
 * 	  1. HandlerExecutionChanin(HandlerMethod and HandlerInterceptors)
 * 	  2. HandlerAdapter: 实际处理 request, 执行 handler
 * --
 * 对 handler 返回值处理的各种类型的集合:
 * @see org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite#returnValueHandlers
 * 对于 @ResponseBody 的处理
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor#supportsReturnType(org.springframework.core.MethodParameter)
 * @see org.springframework.http.converter.HttpMessageConverter 按需转换消息类型: 如 json.
 */
public class SpringWebPreview {
}
