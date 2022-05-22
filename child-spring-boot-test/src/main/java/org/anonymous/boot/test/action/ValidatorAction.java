package org.anonymous.boot.test.action;

import org.anonymous.boot.test.model.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/03
 */
@RestController
@RequestMapping("vali")
public class ValidatorAction {

	/**
	 * @see org.springframework.validation.Validator
	 * @see org.springframework.validation.DataBinder
	 * @see org.springframework.validation.beanvalidation.SpringValidatorAdapter
	 */
	@GetMapping("param")
	Response<String> param(@RequestParam("val") @NotEmpty(message = "val不可为空") String val) {
		Response<String> resp = new Response<>();
		resp.setContent(val);

		return resp;
	}

}
