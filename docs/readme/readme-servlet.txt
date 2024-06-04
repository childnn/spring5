Applications can declare the infrastructure beans listed in Special Bean Types that are required to process requests.
The DispatcherServlet checks the WebApplicationContext for each special bean.
If there are no matching bean types, it falls back on the default types listed in DispatcherServlet.properties.

org.springframework.web.servlet.handler.HandlerMappingIntrospector#initHandlerMappings
org.springframework.web.servlet.DispatcherServlet.DEFAULT_STRATEGIES_PATH
org.springframework.web.servlet.DispatcherServlet.getDefaultStrategies

深刻理解适配器!