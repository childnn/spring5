/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.beans.factory.annotation;

import java.lang.annotation.*;

/**
 * Marks a class as being eligible for Spring-driven configuration.
 *
 * <p>Typically used with the AspectJ {@code AnnotationBeanConfigurerAspect}.
 *
 * @author Rod Johnson
 * @author Rob Harrop
 * @author Adrian Colyer
 * @author Ramnivas Laddad
 * @see org.springframework.beans.factory.aspectj.AnnotationBeanConfigurerAspect
 * @see org.springframework.context.annotation.aspectj.EnableSpringConfigured
 * @see org.springframework.context.annotation.EnableLoadTimeWeaving
 * Do not activate @Configurable processing through the bean configurer aspect unless you really mean to
 * rely on its semantics at runtime. In particular, make sure that you do not
 * use @Configurable on bean classes that are registered as regular Spring beans with the container.
 * Doing so results in double initialization, once through the container and once through the aspect.
 * @since 2.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Configurable {

	/**
	 * The name of the bean definition that serves as the configuration template.
	 */
	String value() default "";

	/**
	 * Are dependencies to be injected via autowiring?
	 */
	Autowire autowire() default Autowire.NO;

	/**
	 * Is dependency checking to be performed for configured objects?
	 */
	boolean dependencyCheck() default false;

	/**
	 * One of the key phrases in the above paragraph is “in essence”.
	 * For most cases, the exact semantics of “after returning from the initialization of a new object” are fine.
	 * In this context, “after initialization” means that the dependencies are injected after the object
	 * has been constructed. This means that the dependencies are not available for use in the constructor
	 * bodies of the class. If you want the dependencies to be injected before the constructor
	 * bodies run and thus be available for use in the body of the constructors,
	 * you need to define this on the @Configurable declaration, as follows:
	 * ---- @Configurable(preConstruction = true)
	 * ----
	 * Are dependencies to be injected prior to the construction of an object?
	 */
	boolean preConstruction() default false;

}
