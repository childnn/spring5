/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.aopalliance.intercept;

import java.lang.reflect.AccessibleObject;

/**
 * This interface represents a generic runtime joinpoint (in the AOP
 * terminology).
 *
 * <p>A runtime joinpoint is an <i>event</i> that occurs on a static
 * joinpoint (i.e. a location in a the program). For instance, an
 * invocation is the runtime joinpoint on a method (static joinpoint).
 * The static part of a given joinpoint can be generically retrieved
 * using the {@link #getStaticPart()} method.
 *
 * <p>In the context of an interception framework, a runtime joinpoint
 * is then the reification of an access to an accessible object (a
 * method, a constructor, a field), i.e. the static part of the
 * joinpoint. It is passed to the interceptors that are installed on
 * the static joinpoint.
 *
 * @author Rod Johnson
 * @see Interceptor
 * 连接点
 * method call	     函数调用	比如调用Logger.info()，这是一处JoinPoint
 * method execution	 函数执行	比如Logger.info()的执行内部，是一处JoinPoint。
 *                              注意它和method call的区别。method call是调用某个函数的地方。
 *                              而execution是某个函数执行的内部。
 * constructor call	 构造函数调用	和method call类似
 * constructor execution 构造函数执行	和method execution类似
 * field get	     获取某个变量	 比如读取User.name成员
 * field set	     设置某个变量	 比如设置User.name成员
 * pre-initialization	Object在构造函数中做得一些工作。
 * initialization	Object在构造函数中做得工作
 * static initialization	类初始化	比如类的static{}
 * handler	异常处理	比如try catch(xxx)中，对应catch内的执行
 * advice execution	这个是AspectJ的内容
 * --
 * 这里列出了AspectJ所认可的JoinPoint的类型。实际上，连接点也就是你想把新的代码插在程序的哪个地方，
 * 是插在构造方法中，还是插在某个方法调用前，或者是插在某个方法中，这个地方就是JoinPoint，
 * 当然，不是所有地方都能给你插的，只有能插的地方，才叫JoinPoint。
 * @see org.aspectj.lang.reflect.Pointcut
 * @see org.springframework.aop.Pointcut
 */
public interface Joinpoint {

	/**
	 * Proceed to the next interceptor in the chain.
	 * <p>The implementation and the semantics of this method depends
	 * on the actual joinpoint type (see the children interfaces).
	 * @return see the children interfaces' proceed definition
	 * @throws Throwable if the joinpoint throws an exception
	 */
	Object proceed() throws Throwable;

	/**
	 * Return the object that holds the current joinpoint's static part.
	 * <p>For instance, the target object for an invocation.
	 * @return the object (can be null if the accessible object is static)
	 */
	Object getThis();

	/**
	 * Return the static part of this joinpoint.
	 * <p>The static part is an accessible object on which a chain of
	 * interceptors are installed.
	 */
	AccessibleObject getStaticPart();

}
