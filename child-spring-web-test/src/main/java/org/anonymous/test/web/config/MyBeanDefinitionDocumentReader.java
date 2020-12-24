package org.anonymous.test.web.config;

import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.w3c.dom.Element;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2020/10/14 20:05
 */
public class MyBeanDefinitionDocumentReader extends DefaultBeanDefinitionDocumentReader {

	@Override
	protected void preProcessXml(Element root) {
		super.preProcessXml(root);
		System.out.println("preProcessXml-root = " + root);
	}

	@Override
	protected void postProcessXml(Element root) {
		super.postProcessXml(root);
		System.out.println("postProcessXml-root = " + root);
	}
}
