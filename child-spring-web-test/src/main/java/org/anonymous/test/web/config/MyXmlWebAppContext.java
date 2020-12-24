package org.anonymous.test.web.config;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2020/10/14 20:04
 */
public class MyXmlWebAppContext extends XmlWebApplicationContext {
	@Override
	protected void initBeanDefinitionReader(XmlBeanDefinitionReader beanDefinitionReader) {
		super.initBeanDefinitionReader(beanDefinitionReader);

		beanDefinitionReader.setDocumentReaderClass(MyBeanDefinitionDocumentReader.class);
	}
}
