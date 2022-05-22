https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#spring-core

Design Philosophy
When you learn about a framework, it’s important to know not only what it does but what principles it follows. Here are the guiding principles of the Spring Framework:

- Provide choice at every level. Spring lets you defer design decisions as late as possible.
  For example, you can switch persistence providers through configuration without changing your code.
  The same is true for many other infrastructure concerns and integration with third-party APIs.
- Accommodate diverse perspectives. Spring embraces flexibility and is not opinionated about how things should be done.
  It supports a wide range of application needs with different perspectives.
- Maintain strong backward compatibility. Spring’s evolution has been carefully managed to force few breaking changes between versions.
  Spring supports a carefully chosen range of JDK versions and third-party libraries to facilitate maintenance of applications and libraries that depend on Spring.
- Care about API design. The Spring team puts a lot of thought and time into making APIs that are intuitive and that hold up across many versions and many years.
- Set high standards for code quality. The Spring Framework puts a strong emphasis on meaningful, current, and accurate javadoc.
   It is one of very few projects that can claim clean code structure with no circular dependencies between packages.

---
关于 spring-boot 相关的整合处理
需要先在 idea 中排除编译 child-spring-boot/child-spring-boot-autoconfigure
中不需要的配置

--- xml-config 会覆盖 java-config
Annotation injection is performed before XML injection.
Thus, the XML configuration overrides the annotations for properties wired through both approaches.
---
<context:annotation-config/> only looks for annotations on beans in the same application context in which it is defined.
This means that, if you put <context:annotation-config/> in a WebApplicationContext for a DispatcherServlet,
it only checks for @Autowired beans in your controllers, and not your services.
See The DispatcherServlet for more information.
---
https://github.com/spring-projects/spring-framework/wiki/Spring-Annotation-Programming-Model
java.lang.reflect.AnnotatedElement
术语:
 1. directly present: 注解 A 直接标注在 element e 上
 2. indirectly present: 注解 A is java.lang.annotation.Repeatable, 注解 A 本身不在 element e 上,
                    但是注解 A 的容器注解在 element e 上.
 3. present: A is directly present on E; or
             No annotation of A 's type is directly present on E, and E is a class,
             and A 's type is inheritable(java.lang.annotation.Inherited),
             and A is present on the superclass of E.
             注解 A 在 element e 上, 或者 注解 A 不直接在 e 上, 且 e 是一个 class
             注解 A 是 java.lang.annotation.Inherited, A 的父类上标注有 A.
 4. associated: A is directly or indirectly present on E; or
                No annotation of A 's type is directly or indirectly present on E,
                and E is a class, and A's type is inheritable,
                and A is associated with the superclass of E.
              A directly present 或者 indirectly present 在 e 上,
              或者, A 不 directly present 或者 不 indirectly present 在 e 上,
              e 是一个 class, A 是 java.lang.annotation.Inherited, 且 A 和 e 的父类 associated
---
static-@Bean-method:

You may declare @Bean methods as static, allowing for them to be called without creating their containing configuration class as an instance. This makes particular sense when defining post-processor beans (for example, of type BeanFactoryPostProcessor or BeanPostProcessor), since such beans get initialized early in the container lifecycle and should avoid triggering other parts of the configuration at that point.
-
Also, be particularly careful with BeanPostProcessor and BeanFactoryPostProcessor definitions through @Bean.
Those should usually be declared as static @Bean methods, not triggering the instantiation of
their containing configuration class. Otherwise, @Autowired and @Value may not work on the configuration class itself,
since it is possible to create it as a bean instance earlier than AutowiredAnnotationBeanPostProcessor.

---
As discussed in the chapter introduction, the `org.springframework.beans.factory` package
provides basic functionality for managing and manipulating beans, including in a programmatic way.
The `org.springframework.context` package adds the ApplicationContext interface,
which extends the BeanFactory interface, in addition to extending other interfaces to
provide additional functionality in a more application framework-oriented style.
Many people use the ApplicationContext in a completely declarative fashion,
not even creating it programmatically, but instead relying on support classes
such as ContextLoader to automatically instantiate an ApplicationContext as part of the normal startup process of a Java EE web application.

To enhance BeanFactory functionality in a more framework-oriented style, the context package also provides the following functionality:

- Access to messages in i18n-style, through the 'MessageSource' interface.

- Access to resources, such as URLs and files, through the ResourceLoader interface.

- Event publication, namely to beans that implement the 'ApplicationListener' interface, through the use of the 'ApplicationEventPublisher' interface.

- Loading of multiple (hierarchical) contexts, letting each be focused on one particular layer,
   such as the web layer of an application, through the 'HierarchicalBeanFactory' interface.

---
An application context is a ResourceLoader, which can be used to load Resource objects.
A Resource is essentially a more feature rich version of the JDK java.net.URL class.
A Resource can obtain low-level resources from almost any location in a transparent fashion,
including from the classpath, a filesystem location, anywhere describable with a standard URL, and some other variations.