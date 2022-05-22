一个 org.springframework.context.ApplicationListener 如何被发现?
1. 前提: 被注册为一个 spring-bean
2. 被特定的 bean-post-processor 处理: org.springframework.context.support.ApplicationListenerDetector
   如果 bean instanceof org.springframework.context.ApplicationListener
   则被加入到: org.springframework.context.ConfigurableApplicationContext#addApplicationListener
一个 org.springframework.context.event.EventListener 方法如何被发现
  org.springframework.context.event.EventListenerMethodProcessor.afterSingletonsInstantiated
