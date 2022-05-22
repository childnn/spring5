package org.anonymous.boot.test.config.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.dao.DataAccessException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/09
 */
@Aspect
public class ChildPointCutAndAdvices {

	//----------------------------------------------------
	// Pointcut
	//----------------------------------------------------

	// Introductions (known as inter-type declarations in AspectJ) enable an aspect to declare
	// that advised objects implement a given interface, and to provide an implementation of that interface on behalf of those objects.
	//
	// You can make an introduction by using the @DeclareParents annotation.
	// This annotation is used to declare that matching types have a new parent (hence the name).
	// For example, given an interface named UsageTracked and an implementation of that interface named DefaultUsageTracked,
	// the following aspect declares that all implementors of service interfaces
	// also implement the UsageTracked interface (e.g. for statistics via JMX):
	@DeclareParents(value = "com.xzy.myapp.service.*+", defaultImpl = DefaultUsageTracked.class)
	public static UsageTracked mixin;

	// The proxy object (this), target object (target), and annotations (@within, @target, @annotation, and @args)
	// can all be bound in a similar fashion. The next two examples show how to match the execution
	// of methods annotated with an @Auditable annotation and extract the audit code:
	//
	// The first of the two examples shows the definition of the @Auditable annotation:
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Auditable {
		enum AuditCode {}

		AuditCode value();
	}

	public interface Sample<T> {

		void sampleGenericMethod(T param);

		void sampleGenericCollectionMethod(Collection<T> param);
	}

	interface UsageTracked {
		default void incrementUseCount() {
		}
	}

	static class DefaultUsageTracked implements UsageTracked {
	}

	static class MyType {
	}

	static class Account {
	}

	// The execution of any public method:
	@Pointcut("execution(public * *(..))")
	private void publicMethod() {
	}

	// The execution of any method with a name that begins with set:
	@Pointcut("execution(* set*(..))")
	private void setMethod() {
	}

	// The execution of any method defined by the AccountService interface:
	@Pointcut("execution(* com.xyz.service.AccountService.*(..))")
	private void AccountServiceMethod() {
	}

	// The execution of any method defined in the service package:
	@Pointcut("execution(* com.xyz.service.*.*(..))")
	private void servicePackage() {
	}

	// The execution of any method defined in the service package or one of its sub-packages:
	@Pointcut("execution(* com.xyz.service..*.*(..))")
	private void serviceOrSubPackage() {
	}

	// Any join point (method execution only in Spring AOP) within the service package:
	@Pointcut("within(com.xyz.service.*)")
	private void servicePackage1() {
	}

	// Any join point (method execution only in Spring AOP) within the service package or one of its sub-packages:
	@Pointcut("within(com.xyz.service..*)")
	private void serviceOrSubPackage1() {
	}

	// Any join point (method execution only in Spring AOP) where the proxy implements the AccountService interface:
	@Pointcut("this(com.xyz.service.AccountService)")
	private void proxyImplAccountService() {
	}

	// Any join point (method execution only in Spring AOP) where the target object implements the AccountService interface:
	@Pointcut("target(com.xyz.service.AccountService)")
	private void targetImplAccountService() {
	}

	//----------------------------------------------------
	// advice
	//----------------------------------------------------

	// Any join point (method execution only in Spring AOP) that takes a single parameter and where the argument passed at runtime is Serializable:
	// Note that the pointcut given in this example is different from execution(* *(java.io.Serializable)).
	// The args version matches if the argument passed at runtime is Serializable,
	// and the execution version matches if the method signature declares a single parameter of type Serializable.
	@Pointcut("args(java.io.Serializable)") // runtime
	private void serialArg() {
	}

	// If we use an in-place pointcut expression, we could rewrite the preceding example as the following example:

	// Any join point (method execution only in Spring AOP) where the target object has a @Transactional annotation:
	@Pointcut("@target(org.springframework.transaction.annotation.Transactional)")
	private void targetTxAnno() {
	}

	// Any join point (method execution only in Spring AOP) where the declared type of the target object has an @Transactional annotation:
	@Pointcut("@within(org.springframework.transaction.annotation.Transactional)")
	private void declaredTxAnno() {
	}

	// You can have multiple advice declarations (and other members as well), all inside the same aspect.
	// We show only a single advice declaration in these examples to focus the effect of each one.

	// Any join point (method execution only in Spring AOP) where the executing method has an @Transactional annotation:
	@Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
	private void methodTxAnno() {
	}

	// Any join point (method execution only in Spring AOP) which takes a single parameter,
	// and where the runtime type of the argument passed has the @Classified annotation:
	@Pointcut("@args(com.xyz.security.Classified)")
	private void argAnno() {
	}

	// Any join point (method execution only in Spring AOP) on a Spring bean named tradeService:
	@Pointcut("bean(tradeService)")
	private void beanName() {
	}

	// Any join point (method execution only in Spring AOP) on Spring beans having names that match the wildcard expression *Service:
	@Pointcut("bean(*Service)")
	private void wildcardBeanName() {
	}

	// Around Advice
	// The last kind of advice is around advice. Around advice runs "around" a matched method’s execution.
	// It has the opportunity to do work both before and after the method runs and to determine when, how,
	// and even if the method actually gets to run at all.
	// Around advice is often used if you need to share state before and after a method execution in a thread-safe manner
	// – for example, starting and stopping a timer.
	// Always use the least powerful form of advice that meets your requirements.
	// For example, do not use around advice if before advice is sufficient for your needs.

	// Advice is associated with a pointcut expression and runs before, after, or around method executions matched by the pointcut.
	// The pointcut expression may be either a simple reference to a named pointcut or a pointcut expression declared in place.
	@Before("com.xyz.myapp.CommonPointcuts.dataAccessOperation()")
	public void doAccessCheck() {
		// ...
	}

	// Any advice method may declare, as its first parameter, a parameter of type org.aspectj.lang.JoinPoint.
	// Note that around advice is required to declare a first parameter of type ProceedingJoinPoint, which is a subclass of JoinPoint.
	// The JoinPoint interface provides a number of useful methods:
	// getArgs(): Returns the method arguments.
	// getThis(): Returns the proxy object. 代理对象
	// getTarget(): Returns the target object. 目标对象
	// getSignature(): Returns a description of the method that is being advised.
	// toString(): Prints a useful description of the method being advised.

	// Passing Parameters to Advice

	@Before("execution(* com.xyz.myapp.dao.*.*(..))")
	public void doAccessCheckWithPointcutExpression() {
		// ...
	}

	// After returning advice runs when a matched method execution returns normally.
	// You can declare it by using the @AfterReturning annotation
	@AfterReturning("com.xyz.myapp.CommonPointcuts.dataAccessOperation()")
	public void doAccessCheckAfterReturning() {
		// ...
	}

	// Sometimes, you need access in the advice body to the actual value that was returned.
	// You can use the form of @AfterReturning that binds the return value to get that access, as the following example shows:
	// The name used in the returning attribute must correspond to the name of a parameter in the advice method.
	// When a method execution returns, the return value is passed to the advice method as the corresponding argument value.
	// A returning clause also restricts matching to only those method executions
	// that return a value of the specified type (in this case, Object, which matches any return value).
	// Please note that it is not possible to return a totally different reference when using after returning advice.
	@AfterReturning(
			pointcut = "com.xyz.myapp.CommonPointcuts.dataAccessOperation()",
			returning = "retVal")
	public void doAccessCheck(Object retVal) {
		// ...
	}

	// After throwing advice runs when a matched method execution exits by throwing an exception.
	// You can declare it by using the @AfterThrowing annotation, as the following example shows:
	@AfterThrowing("com.xyz.myapp.CommonPointcuts.dataAccessOperation()")
	public void doRecoveryActions() {
		// ...
	}

	// Often, you want the advice to run only when exceptions of a given type are thrown,
	// and you also often need access to the thrown exception in the advice body.
	// You can use the throwing attribute to both restrict matching (if desired--use
	// Throwable as the exception type otherwise) and bind the thrown exception to an advice parameter.
	// The following example shows how to do so:
	// The name used in the throwing attribute must correspond to the name of a parameter in the advice method.
	// When a method execution exits by throwing an exception, the exception is passed to the advice method as the corresponding argument value.
	// A throwing clause also restricts matching to only those method executions that
	// throw an exception of the specified type (DataAccessException, in this case).
	//
	// Note that @AfterThrowing does not indicate a general exception handling callback.
	// Specifically, an @AfterThrowing advice method is only supposed to receive exceptions
	// from the join point (user-declared target method) itself but not from an accompanying @After/@AfterReturning method.
	@AfterThrowing(
			pointcut = "com.xyz.myapp.CommonPointcuts.dataAccessOperation()",
			throwing = "ex")
	public void doRecoveryActions(DataAccessException ex) {
		// ...
	}

	// Advice Parameters and Generics
	// Spring AOP can handle generics used in class declarations and method parameters.
	// Suppose you have a generic type like the following:

	// After (finally) advice runs when a matched method execution exits. It is declared by using the @After annotation.
	// After advice must be prepared to handle both normal and exception return conditions.
	// It is typically used for releasing resources and similar purposes.
	// The following example shows how to use after finally advice:
	//
	// Note that @After advice in AspectJ is defined as "after finally advice", analogous to a finally block in a try-catch statement.
	// It will be invoked for any outcome, normal return or exception thrown from the join point (user-declared target method),
	// in contrast to @AfterReturning which only applies to successful normal returns.
	@After("com.xyz.myapp.CommonPointcuts.dataAccessOperation()")
	public void doReleaseLock() {
		// ...
	}

	// Around advice is declared by annotating a method with the @Around annotation.
	// The method should declare Object as its return type, and the first parameter of the method must be of type ProceedingJoinPoint.
	// Within the body of the advice method, you must invoke proceed() on the ProceedingJoinPoint in order for the underlying method to run.
	// Invoking proceed() without arguments will result in the caller’s original arguments being supplied to the underlying method when it is invoked.
	// For advanced use cases, there is an overloaded variant of the proceed() method which accepts an array of arguments (Object[]).
	// The values in the array will be used as the arguments to the underlying method when it is invoked.
	//
	// The behavior of proceed when called with an Object[] is a little different than the behavior of proceed for
	// around advice compiled by the AspectJ compiler. For around advice written using the traditional AspectJ language,
	// the number of arguments passed to proceed must match the number of arguments passed to the around advice
	// (not the number of arguments taken by the underlying join point),
	// and the value passed to proceed in a given argument position supplants the original value at the
	// join point for the entity the value was bound to (do not worry if this does not make sense right now).
	// The approach taken by Spring is simpler and a better match to its proxy-based,
	// execution-only semantics. You only need to be aware of this difference if you compile @AspectJ
	// aspects written for Spring and use proceed with arguments with the AspectJ compiler and weaver.
	// There is a way to write such aspects that is 100% compatible across both Spring AOP and AspectJ
	//
	// The value returned by the around advice is the return value seen by the caller of the method.
	// For example, a simple caching aspect could return a value from a cache if it has one or invoke proceed()
	// (and return that value) if it does not.
	// Note that proceed may be invoked once, many times,
	// or not at all within the body of the around advice. All of these are legal.
	// If you declare the return type of your around advice method as void, null will always be returned to the caller,
	// effectively ignoring the result of any invocation of proceed().
	// It is therefore recommended that an around advice method declare a return type of Object.
	// The advice method should typically return the value returned from an invocation of proceed(),
	// even if the underlying method has a void return type. However, the advice may optionally return a cached value,
	// a wrapped value, or some other value depending on the use case.
	@Around("com.xyz.myapp.CommonPointcuts.businessService()")
	public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
		// start stopwatch
		Object retVal = pjp.proceed();
		// stop stopwatch
		return retVal;
	}

	// We have already seen how to bind the returned value or exception value (using after returning and after throwing advice).
	// To make argument values available to the advice body, you can use the binding form of args.
	// If you use a parameter name in place of a type name in an args expression,
	// the value of the corresponding argument is passed as the parameter value when the advice is invoked.
	// An example should make this clearer. Suppose you want to advise the execution of DAO operations
	// that take an Account object as the first parameter, and you need access to the account in the advice body.
	// You could write the following:
	@Before("com.xyz.myapp.CommonPointcuts.dataAccessOperation() && args(account,..)")
	public void validateAccount(Account account) {
		// ...
	}

	// To make this work, we would have to inspect every element of the collection, which is not reasonable,
	// as we also cannot decide how to treat null values in general.
	// To achieve something similar to this,
	// you have to type the parameter to Collection<?> and manually check the type of the elements.

	// The args(account,..) part of the pointcut expression serves two purposes.
	// First, it restricts matching to only those method executions where the method takes at least one parameter,
	// and the argument passed to that parameter is an instance of Account.
	// Second, it makes the actual Account object available to the advice through the account parameter.
	//
	// Another way of writing this is to declare a pointcut that "provides" the Account object value when it matches a join point,
	// and then refer to the named pointcut from the advice. This would look as follows:
	@Pointcut("com.xyz.myapp.CommonPointcuts.dataAccessOperation() && args(account,..)")
	private void accountDataAccessOperation(Account account) {
	}

	// @Before("accountDataAccessOperation(account)")
	@Before(value = "accountDataAccessOperation(account)", argNames = "account")
	public void validateAccount1(Account account) {
		// ...
	}

	// The second of the two examples shows the advice that matches the execution of @Auditable methods:
	@Before("com.xyz.lib.Pointcuts.anyPublicMethod() && @annotation(auditable)")
	public void audit(Auditable auditable) {
		Auditable.AuditCode code = auditable.value();
		// ...
	}

	// Using the argNames attribute is a little clumsy, so if the argNames attribute has not been specified,
	// Spring AOP looks at the debug information for the class and tries to determine the parameter names from the local variable table.
	// This information is present as long as the classes have been compiled with debug information (-g:vars at a minimum).
	// The consequences of compiling with this flag on are:
	// (1) your code is slightly easier to understand (reverse engineer),
	// (2) the class file sizes are very slightly bigger (typically inconsequential),
	// (3) the optimization to remove unused local variables is not applied by your compiler.
	// In other words, you should encounter no difficulties by building with this flag on.

	// 	If an @AspectJ aspect has been compiled by the AspectJ compiler (ajc) even without the debug information,
	// 	you need not add the argNames attribute, as the compiler retain the needed information.

	// If the code has been compiled without the necessary debug information,
	// Spring AOP tries to deduce the pairing of binding variables to parameters
	// (for example, if only one variable is bound in the pointcut expression, and the advice method takes only one parameter, the pairing is obvious).
	// If the binding of variables is ambiguous given the available information, an AmbiguousBindingException is thrown.

	// If all of the above strategies fail, an IllegalArgumentException is thrown.

	// Proceeding with Arguments

	// You can restrict interception of method types to certain parameter types by tying the advice parameter
	// to the parameter type for which you want to intercept the method:
	@Before("execution(* ..Sample+.sampleGenericMethod(*)) && args(param)")
	public void beforeSampleMethod(MyType param) {
		// Advice implementation
	}

	// This approach does not work for generic collections. So you cannot define a pointcut as follows:
	@Before("execution(* ..Sample+.sampleGenericCollectionMethod(*)) && args(param)")
	public void beforeSampleMethod(Collection<MyType> param) {
		// Advice implementation
	}

	// In many cases, you do this binding anyway (as in the preceding example).


	// Advice Ordering

	// What happens when multiple pieces of advice all want to run at the same join point?
	// Spring AOP follows the same precedence rules as AspectJ to determine the order of advice execution.
	// The highest precedence advice runs first "on the way in" (so, given two pieces of [before advice], the one with highest precedence runs first).
	// "On the way out" from a join point, the highest precedence advice runs last
	// (so, given two pieces of [after advice], the one with the highest precedence will run second).
	//
	// When two pieces of advice defined in different aspects both need to run at the same join point,
	// unless you specify otherwise, the order of execution is undefined. You can control the order of execution by specifying precedence.
	// This is done in the normal Spring way by either implementing the org.springframework.core.Ordered
	// interface in the aspect class or annotating it with the @Order annotation.
	// Given two aspects, the aspect returning the lower value from Ordered.getOrder() (or the annotation value) has the higher precedence.

	// Each of the distinct advice types of a particular aspect is conceptually meant to apply to the join point directly.
	// As a consequence, an @AfterThrowing advice method is not supposed to receive an exception from an accompanying @After/@AfterReturning method.
	//
	// As of Spring Framework 5.2.7, advice methods defined in the same @Aspect class that need to run at the same join point
	// are assigned precedence based on their advice type in the following order, from highest to lowest precedence:
	// @Around, @Before, @After, @AfterReturning, @AfterThrowing.
	// Note, however, that an @After advice method will effectively be invoked after any @AfterReturning or @AfterThrowing advice methods
	// in the same aspect, following AspectJ’s "after finally advice" semantics for @After.
	//
	// When two pieces of the same type of advice (for example, two @After advice methods) defined in the same @Aspect class
	// both need to run at the same join point, the ordering is undefined
	// (since there is no way to retrieve the source code declaration order through reflection for javac-compiled classes).
	// Consider collapsing such advice methods into one advice method per join point in each @Aspect class or refactor
	// the pieces of advice into separate @Aspect classes that you can order at the aspect level via Ordered or @Order.


	// Introductions

	// Determining Argument Names
	// The parameter binding in advice invocations relies on matching names used in pointcut
	// expressions to declared parameter names in advice and pointcut method signatures.
	// Parameter names are not available through Java reflection,
	// so Spring AOP uses the following strategy to determine parameter names:
	// If the parameter names have been explicitly specified by the user, the specified parameter names are used.
	// Both the advice and the pointcut annotations have an optional argNames attribute that you can use to
	// specify the argument names of the annotated method. These argument names are available at runtime.
	// The following example shows how to use the argNames attribute:
	@Before(value = "com.xyz.lib.Pointcuts.anyPublicMethod() && target(bean) && @annotation(auditable)",
			argNames = "bean,auditable")
	public void audit(Object bean, Auditable auditable) {
		Auditable.AuditCode code = auditable.value();
		// ... use code and bean
	}

	// If the first parameter is of the JoinPoint, ProceedingJoinPoint, or JoinPoint.StaticPart type,
	// you can leave out the name of the parameter from the value of the argNames attribute.
	// For example, if you modify the preceding advice to receive the join point object, the argNames attribute need not include it:
	@Before(value = "com.xyz.lib.Pointcuts.anyPublicMethod() && target(bean) && @annotation(auditable)",
			argNames = "bean,auditable")
	public void audit(JoinPoint jp, Object bean, Auditable auditable) {
		Auditable.AuditCode code = auditable.value();
		// ... use code, bean, and jp
	}

	// The interface to be implemented is determined by the type of the annotated field.
	// The value attribute of the @DeclareParents annotation is an AspectJ type pattern.
	// Any bean of a matching type implements the UsageTracked interface.
	// Note that, in the before advice of the preceding example,
	// service beans can be directly used as implementations of the UsageTracked interface.
	// If accessing a bean programmatically, you would write the following:
	// UsageTracked usageTracked = (UsageTracked) context.getBean("myService");

	// The special treatment given to the first parameter of the JoinPoint, ProceedingJoinPoint,
	// and JoinPoint.StaticPart types is particularly convenient for advice instances
	// that do not collect any other join point context. In such situations, you may omit the argNames attribute.
	// For example, the following advice need not declare the argNames attribute:
	@Before("com.xyz.lib.Pointcuts.anyPublicMethod()")
	public void audit(JoinPoint jp) {
		// ... use jp
	}

	// We remarked earlier that we would describe how to write a proceed call with arguments that works consistently across Spring AOP and AspectJ.
	// The solution is to ensure that the advice signature binds each of the method parameters in order.
	// The following example shows how to do so:
	@Around("execution(java.util.List<Account> find*(..)) && " +
			"com.xyz.myapp.CommonPointcuts.inDataAccessLayer() && " +
			"args(accountHolderNamePattern)")
	public Object preProcessQueryPattern(ProceedingJoinPoint pjp, String accountHolderNamePattern) throws Throwable {
		String newPattern = preProcess(accountHolderNamePattern);
		return pjp.proceed(new Object[]{newPattern});
	}

	private String preProcess(String accountHolderNamePattern) {
		return null;
	}

	@Before(value = "com.xyz.myapp.CommonPointcuts.businessService() && this(usageTracked)", argNames = "usageTracked")
	public void recordUsage(UsageTracked usageTracked) {
		usageTracked.incrementUseCount();
	}

}
