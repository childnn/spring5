AOP Concepts: 术语

1. Aspect  -- @org.aspectj.lang.annotation.Aspect
   Aspect被翻译方面或者切面，相当于OOP中的类，就是封装用于横插入系统的功能。例如日志、事务、安全验证等。
    A modularization of a concern that cuts across multiple classes.
    Transaction management is a good example of a crosscutting concern in enterprise Java applications.
    In Spring AOP, aspects are implemented by using regular classes (the schema-based approach) or
    regular classes annotated with the @Aspect annotation (the @AspectJ style).
2. JoinPoint -- 具体被拦截方法
   JoinPoint(连接点)是AOP中的一个重要的关键概念。JoinPoint可以看做是程序运行时的一个执行点。
   打个比方，比如执行System.out.println("Hello")这个函数，println()就是一个joinpoint；
   再如给一个变量赋值也是一个joinpoint；还有最常用的for循环，也是一个joinpoint。

    A point during the execution of a program, such as the execution of a method or the handling of an exception.
    In Spring AOP, a join point always represents a method execution.

   理论上说，一个程序中很多地方都可以被看做是JoinPoint，但是AspectJ中，只有下面所示的几种执行点被认为是JoinPoint：

   JoinPoint的类型
   JoinPoint	说明	示例
   method call	函数调用	比如调用Logger.info()，这是一处JoinPoint
   method execution	函数执行	比如Logger.info()的执行内部，是一处JoinPoint。注意它和method call的区别。
                            method call是调用某个函数的地方。而execution是某个函数执行的内部。
   constructor call	构造函数调用	和method call类似
   constructor execution	构造函数执行	和method execution类似
   field get	获取某个变量	比如读取User.name成员
   field set	设置某个变量	比如设置User.name成员
   pre-initialization	Object在构造函数中做得一些工作。
   initialization	Object在构造函数中做得工作
   static initialization	类初始化	比如类的static{}
   handler	异常处理	比如 try catch(xxx) 中，对应 catch 内的执行
   advice execution	这个是 AspectJ 的内容
   这里列出了 AspectJ 所认可的 JoinPoint 的类型。实际上，连接点也就是你想把新的代码插在程序的哪个地方，
      是插在构造方法中，还是插在某个方法调用前，或者是插在某个方法中，这个地方就是JoinPoint，
      当然，不是所有地方都能给你插的，只有能插的地方，才叫 JoinPoint
3. PointCut -- @org.aspectj.lang.annotation.Pointcut -- 满足某类条件的方法
   在广义的 aop 中, PointCut 也可以是 field 等非 method, 在 spring 中只能是 method.
   A predicate that matches join points. Advice is associated with a pointcut expression
   and runs at any join point matched by the pointcut (for example, the execution of a method with a certain name).
   The concept of join points as matched by pointcut expressions is central to AOP,
   and Spring uses the AspectJ pointcut expression language by default.

   PointCut通俗地翻译为切入点，一个程序会有多个Join Point，即使同一个函数，
   也还分为call和execution类型的Join Point，但并不是所有的Join Point都是我们关心的，
   Pointcut就是提供一种使得开发者能够选择自己需要的JoinPoint的方法。
   PointCut分为 call、execution、target、this、within等关键字。与joinPoint相比，pointcut就是一个具体的切点。
4. Advice
   Action taken by an aspect at a particular join point.
   Different types of advice include “around”, “before” and “after” advice. (Advice types are discussed later.)
   Many AOP frameworks, including Spring, model an advice as an interceptor
   and maintain a chain of interceptors around the join point.

   Spring AOP includes the following types of advice:

   Before advice: Advice that runs before a join point but that does not have the ability to
                prevent execution flow proceeding to the join point (unless it throws an exception).

   After returning advice: Advice to be run after a join point completes normally
            (for example, if a method returns without throwing an exception).

   After throwing advice: Advice to be run if a method exits by throwing an exception.

   After (finally) advice: Advice to be run regardless of the means by which a join point exits (normal or exceptional return).

   Around advice: Advice that surrounds a join point such as a method invocation.
                This is the most powerful kind of advice.
                Around advice can perform custom behavior before and after the method invocation.
                It is also responsible for choosing whether to proceed to the join point
                or to shortcut the advised method execution by returning its own return value or throwing an exception.

   Advice翻译为通知或者增强(Advisor)，就是我们插入的代码以何种方式插入，相当于OOP中的方法，有Before、After以及Around。
   Before
   前置通知用于将切面代码插入方法之前，也就是说，在方法执行之前，会首先执行前置通知里的代码.包含前置通知代码的类就是切面。
   After -- @org.aspectj.lang.annotation.After
   后置通知的代码在调用被拦截的方法后调用。
   Around
   环绕通知能力最强，可以在方法调用前执行通知代码，可以决定是否还调用目标方法。
   也就是说它可以控制被拦截的方法的执行，还可以控制被拦截方法的返回值。
   AfterReturning -- @org.aspectj.lang.annotation.AfterReturning
   正常返回执行
   AfterThrowing -- @org.aspectj.lang.annotation.AfterThrowing
   异常时执行

  * 通知的执行顺序：(不包括 环绕)
  *      1) before: 前置. 无论如何,都会执行,且都会第一个执行
  *      2) after-returning: 后置. 异常时,不会执行
  *      3) after-throwing: 异常. 异常时,才会执行
  *      4) after: 最终. 无论如何都会执行(不一定是最后一个执行, 最终不代表最后) -- 相当于 finally
  *   1. xml 方式:
  *       1) before: 一定会第一个执行, 与标签顺序无关
  *       2) after: 一定会执行, 但顺序不一定(但一定不会在 before 之前执行)
  *       3) after, after-throwing, after-returning
  *          a) after-throwing 与 after-returning 不会共存(永远只会有一个会执行)
  *          b) after 与 after-throwing, after 与 after-returning 的执行顺序, 与 xml 中的定义顺序有关
  *
  *   2. 注解方式:
  *      1) @Before: 一定会第一个执行的 通知, 与注解顺序无关
  *      2) @After: 一定会执行, 一定会紧接着 切入点之后 执行 (与 xml 不同的地方)
  *      3) @AfterThrowing/@AfterReturning: 不会同时执行, 但一定在 @After 之后执行

5. Target
   Target指的是需要切入的目标类或者目标接口。
   An object being advised by one or more aspects. Also referred to as the “advised object”.
   Since Spring AOP is implemented by using runtime proxies, this object is always a proxied object.
6. Proxy
   Proxy是代理，AOP工作时是通过代理对象来访问目标对象。其实AOP的实现是通过动态代理，离不开代理模式，所以必须要有一个代理对象。
   An object created by the AOP framework in order to implement the aspect contracts (advise method executions and so on).
   In the Spring Framework, an AOP proxy is a JDK dynamic proxy or a CGLIB proxy.
7. Weaving
   Weaving即织入，在目标对象中插入切面代码的过程就叫做织入。
    linking aspects with other application types or objects to create an advised object.
    This can be done at compile time (using the AspectJ compiler, for example), load time, or at runtime.
    Spring AOP, like other pure Java AOP frameworks, performs weaving at runtime.
8. Introduction:
    Declaring additional methods or fields on behalf of a type.
    Spring AOP lets you introduce new interfaces (and a corresponding implementation) to any advised object.
    For example, you could use an introduction to make a bean implement an IsModified interface, to simplify caching.
    (An introduction is known as an inter-type declaration in the AspectJ community.)

org.springframework.aop.aspectj.AspectJAdviceParameterNameDiscoverer
org.springframework.aop.aspectj.annotation.AbstractAspectJAdvisorFactory


Pointcut 标识符:

Supported Pointcut Designators
Spring AOP supports the following AspectJ pointcut designators (PCD) for use in pointcut expressions:

   -- execution: For matching method execution join points.
            This is the primary pointcut designator to use when working with Spring AOP.
   -- within: Limits matching to join points within certain types
            (the execution of a method declared within a matching type when using Spring AOP).
   -- this: Limits matching to join points (the execution of methods when using Spring AOP)
            where the bean reference (Spring AOP proxy) is an instance of the given type.
   -- target: Limits matching to join points (the execution of methods when using Spring AOP)
            where the target object (application object being proxied) is an instance of the given type.
   -- args: Limits matching to join points (the execution of methods when using Spring AOP)
            where the arguments are instances of the given types.
   -- @target: Limits matching to join points (the execution of methods when using Spring AOP)
                where the class of the executing object has an annotation of the given type.
   -- @args: Limits matching to join points (the execution of methods when using Spring AOP)
            where the runtime type of the actual arguments passed have annotations of the given types.
   -- @within: Limits matching to join points within types that have the given annotation
                (the execution of methods declared in types with the given annotation when using Spring AOP).
   -- @annotation: Limits matching to join points where the subject of the join point
                (the method being run in Spring AOP) has the given annotation.

Other pointcut types
    The full AspectJ pointcut language supports additional pointcut designators that are not supported in Spring:
    call, get, set, preinitialization, staticinitialization, initialization, handler,
    adviceexecution, withincode, cflow, cflowbelow, if, @this, and @withincode.
    Use of these pointcut designators in pointcut expressions interpreted
    by Spring AOP results in an IllegalArgumentException being thrown.

The set of pointcut designators supported by Spring AOP may be extended in future releases to support more of the AspectJ pointcut designators.

Spring AOP also supports an additional PCD named bean.
This PCD lets you limit the matching of join points to a particular named Spring bean or to a set of named Spring beans (when using wildcards).
The bean PCD has the following form:
        bean(idOrNameOfBean)
The idOrNameOfBean token can be the name of any Spring bean.
Limited wildcard support that uses the * character is provided, so,
if you establish some naming conventions for your Spring beans, you can write a bean PCD expression to select them.
As is the case with other pointcut designators, the bean PCD can be used with the && (and), || (or), and ! (negation) operators, too.
----
Combining Pointcut Expressions
You can combine pointcut expressions by using &&, || and !.
You can also refer to pointcut expressions by name.
The following example shows three pointcut expressions:


// anyPublicOperation matches if a method execution join point represents the execution
//   of any public method.
@Pointcut("execution(public * *(..))")
private void anyPublicOperation() {}

// inTrading matches if a method execution is in the trading module.
@Pointcut("within(com.xyz.myapp.trading..*)")
private void inTrading() {}

// tradingOperation matches if a method execution represents any public method in the
//   trading module.
@Pointcut("anyPublicOperation() && inTrading()")
private void tradingOperation() {}

It is a best practice to build more complex pointcut expressions out of smaller named components, as shown earlier.
When referring to pointcuts by name, normal Java visibility rules apply
(you can see private pointcuts in the same type, protected pointcuts in the hierarchy, public pointcuts anywhere, and so on).
Visibility does not affect pointcut matching.
-----
Spring AOP users are likely to use the execution pointcut designator the most often. The format of an execution expression follows:

  execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern) throws-pattern?)

All parts except the returning type pattern (ret-type-pattern in the preceding snippet), the name pattern, and the parameters pattern are optional.
The returning type pattern determines what the return type of the method must be in order for a join point to be matched.
 * is most frequently used as the returning type pattern. It matches any return type.
The parameters pattern is slightly more complex:
    () matches a method that takes no parameters,
    whereas (..) matches any number (zero or more) of parameters.
    The (*) pattern matches a method that takes one parameter of any type.
    (*,String) matches a method that takes two parameters. The first can be of any type, while the second must be a String.