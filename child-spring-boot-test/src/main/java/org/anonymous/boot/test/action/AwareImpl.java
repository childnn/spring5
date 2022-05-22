package org.anonymous.boot.test.action;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.*;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringValueResolver;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @see org.springframework.beans.factory.Aware
 * @since 2022/05/02
 */
public class AwareImpl implements ApplicationContextAware, BeanFactoryAware, BeanNameAware,
		MessageSourceAware, BeanClassLoaderAware, ApplicationEventPublisherAware,
		EmbeddedValueResolverAware, EnvironmentAware, ImportAware {

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {

	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

	}

	@Override
	public void setBeanName(String name) {

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

	}

	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {

	}

	@Override
	public void setEnvironment(Environment environment) {

	}

	@Override
	public void setMessageSource(MessageSource messageSource) {

	}

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {

	}
}
