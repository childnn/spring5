package org.anonymous.boot.test.validator;

import org.anonymous.boot.test.model.Person;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/03
 */
public class PersonValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Person.class.equals(clazz);
	}

	/**
	 * @param obj the object that is to be validated
	 * @param e   contextual state about the validation process
	 * @see ValidationUtils
	 */
	@Override
	public void validate(Object obj, Errors e) {
		// The static rejectIfEmpty(..) method on the ValidationUtils class is
		// used to reject the name property if it is null or the empty string.
		// Have a look at the ValidationUtils javadoc to see what functionality
		// it provides besides the example shown previously.
		ValidationUtils.rejectIfEmpty(e, "name", "name.empty");
		Person p = (Person) obj;
		if (p.getAge() < 0) {
			e.rejectValue("age", "negative value");
		} else if (p.getAge() > 110) {
			e.rejectValue("age", "too.darn.old");
		}
	}

}
