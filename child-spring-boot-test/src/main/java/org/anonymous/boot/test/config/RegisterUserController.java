package org.anonymous.boot.test.config;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.web.bind.ServletRequestDataBinder;

import javax.servlet.http.HttpServletRequest;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/03
 */
public class RegisterUserController {
	private final PropertyEditorRegistrar customPropertyEditorRegistrar;

	public RegisterUserController(PropertyEditorRegistrar propertyEditorRegistrar) {
		this.customPropertyEditorRegistrar = propertyEditorRegistrar;
	}

	protected void initBinder(
			HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		this.customPropertyEditorRegistrar.registerCustomEditors(binder);
	}

}
