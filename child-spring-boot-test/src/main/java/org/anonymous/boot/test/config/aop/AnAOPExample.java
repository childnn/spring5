package org.anonymous.boot.test.config.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.dao.PessimisticLockingFailureException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/15
 */
@Aspect
public class AnAOPExample implements Ordered {

	// Now that you have seen how all the constituent parts work, we can put them together to do something useful.
	//
	// The execution of business services can sometimes fail due to concurrency issues (for example, a deadlock loser).
	// If the operation is retried, it is likely to succeed on the next try.
	// For business services where it is appropriate to retry in such conditions
	// (idempotent operations that do not need to go back to the user for conflict resolution),
	// we want to transparently retry the operation to avoid the client seeing a PessimisticLockingFailureException.
	// This is a requirement that clearly cuts across multiple services in the service layer and, hence,
	// is ideal for implementing through an aspect.
	//
	// Because we want to retry the operation, we need to use around advice so that we can call proceed multiple times.
	// The following listing shows the basic aspect implementation:

	// Note that the aspect implements the Ordered interface so that we can set the precedence of
	// the aspect higher than the transaction advice (we want a fresh transaction each time we retry).
	// The maxRetries and order properties are both configured by Spring.
	// The main action happens in the doConcurrentOperation around advice.
	// Notice that, for the moment, we apply the retry logic to each businessService().
	// We try to proceed, and if we fail with a PessimisticLockingFailureException, we try again,
	// unless we have exhausted all of our retry attempts.

	// <aop:aspectj-autoproxy/>
	//
	// <bean id="concurrentOperationExecutor" class="com.xyz.myapp.service.impl.ConcurrentOperationExecutor">
	//     <property name="maxRetries" value="3"/>
	//     <property name="order" value="100"/>
	// </bean>

	// <aop:config>
	//
	//     <aop:aspect id="concurrentOperationRetry" ref="concurrentOperationExecutor">
	//
	//         <aop:pointcut id="idempotentOperation"
	//             expression="execution(* com.xyz.myapp.service.*.*(..))"/>
	//
	//         <aop:around
	//             pointcut-ref="idempotentOperation"
	//             method="doConcurrentOperation"/>
	//
	//     </aop:aspect>
	//
	// </aop:config>


	// To refine the aspect so that it retries only idempotent operations, we might define the following Idempotent annotation:

	private static final int DEFAULT_MAX_RETRIES = 2;
	private int maxRetries = DEFAULT_MAX_RETRIES;
	private int order = 1;

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Idempotent {
		// marker annotation
	}

	// We can then use the annotation to annotate the implementation of service operations.
	// The change to the aspect to retry only idempotent operations involves refining
	// the pointcut expression so that only @Idempotent operations match, as follows:
	@Around("com.xyz.myapp.CommonPointcuts.businessService() && @annotation(org.anonymous.boot.test.config.aop.AnAOPExample.Idempotent)")
	public Object doConcurrentOperation1(ProceedingJoinPoint pjp) throws Throwable {
		// ...

		return null;
	}

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Around("com.xyz.myapp.CommonPointcuts.businessService()")
	public Object doConcurrentOperation(ProceedingJoinPoint pjp) throws Throwable {
		int numAttempts = 0;
		PessimisticLockingFailureException lockFailureException;
		do {
			numAttempts++;
			try {
				return pjp.proceed();
			} catch (PessimisticLockingFailureException ex) {
				lockFailureException = ex;
			}
		} while (numAttempts <= this.maxRetries);

		throw lockFailureException;
	}

}
