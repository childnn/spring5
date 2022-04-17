1. Aspect
   Aspect被翻译方面或者切面，相当于OOP中的类，就是封装用于横插入系统的功能。例如日志、事务、安全验证等。
2. JoinPoint
   JoinPoint(连接点)是AOP中的一个重要的关键概念。JoinPoint可以看做是程序运行时的一个执行点。
   打个比方，比如执行System.out.println("Hello")这个函数，println()就是一个joinpoint；
   再如给一个变量赋值也是一个joinpoint；还有最常用的for循环，也是一个joinpoint。


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
3. PointCut
   PointCut通俗地翻译为切入点，一个程序会有多个Join Point，即使同一个函数，
   也还分为call和execution类型的Join Point，但并不是所有的Join Point都是我们关心的，
   Pointcut就是提供一种使得开发者能够选择自己需要的JoinPoint的方法。
   PointCut分为 call、execution、target、this、within等关键字。与joinPoint相比，pointcut就是一个具体的切点。
4. Advice
   Advice翻译为通知或者增强(Advisor)，就是我们插入的代码以何种方式插入，相当于OOP中的方法，有Before、After以及Around。
   Before
   前置通知用于将切面代码插入方法之前，也就是说，在方法执行之前，会首先执行前置通知里的代码.包含前置通知代码的类就是切面。
   After
   后置通知的代码在调用被拦截的方法后调用。
   Around
   环绕通知能力最强，可以在方法调用前执行通知代码，可以决定是否还调用目标方法。
   也就是说它可以控制被拦截的方法的执行，还可以控制被拦截方法的返回值。
5. Target
   Target指的是需要切入的目标类或者目标接口。
6. Proxy
   Proxy是代理，AOP工作时是通过代理对象来访问目标对象。其实AOP的实现是通过动态代理，离不开代理模式，所以必须要有一个代理对象。
7. Weaving
   Weaving即织入，在目标对象中插入切面代码的过程就叫做织入。

org.springframework.aop.aspectj.AspectJAdviceParameterNameDiscoverer
org.springframework.aop.aspectj.annotation.AbstractAspectJAdvisorFactory