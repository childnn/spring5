aspect: 切面
    joinpoint: 连接点(被拦截的方法)
        point during the execution of a program, such as a method invocation or a particular
        exception being thrown. In Spring AOP, a joinpoint is always method invocation.
        Spring does not use the term joinpoint prominently; jointpoint information is accessible
        through methods on the {org.aopalliance.intercept.MethodInvocation} argument passed to
        interceptors, and is evaluated by implementations of the {org.springframework.aop.Pointcut}
        interface.
    advice: 通知
        action taken by the AOP framework at a particular joinpoint. Different types of advice include
        "around", "before", "throws" advice.
    pointcut: 切入点(连接点的集合)
        a set of jointpoints specifying when an advice should fire. An AOP framework must allow
        developers to specify pointcuts: for example, using regular expressions.
    introduction:
        Adding methods or fields to an advised class. Spring allows you to introduce
        new interfaces to any advised object. For example, you could use an introduction to make any
        object implement an {IsModified} interface, to simplify caching.
    target object: 目标对象
        Object containing the joinpoint. Also referred to as advised or proxied object.
    AOP proxy:
        Object created by the AOP framework, including advice. In Spring, an AOP proxy will be a JDK
        dynamic proxy or a CGLIB proxy.
    weaving: 织入
        assembling aspects to create an advised object. This can be done at compile time(using the
        AspectJ compiler, for example), or at runtime. Spring, like other pure Java AOP frameworks,
        performs weaving at runtime.
---
Different advice types include:
    around advice: 环绕通知
                advice that surrounds a joinpoint such as a method invocation. This is the most powerful
                kind of advice. Around advices will perform custom behaviour before and after the method
                invocation. They are responsible for choosing whether to proceed to the joinpoint or to
                shortcut execution by returning their own return value or throwing an exception.
    before advice: 前置通知
               advice that executes before a joinpoint, but which does not have the ability to prevent
               execution flow proceeding to the joinpoint(unless it throws an exception).
    throws advice: 异常通知
              advice to be executed if a method throws an exception. Spring provides strongly typed
              throws advice, so you can write code that catches the exception(and subclasses) you're
              interested in, without needing to cast from Throwable or Exception.
    after returning advice: 后置通知
              advice to be executed after a joinpoint completes normally: for example, if a method
              returns without throwing an exception.
