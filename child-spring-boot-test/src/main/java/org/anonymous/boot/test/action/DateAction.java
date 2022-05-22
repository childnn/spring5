package org.anonymous.boot.test.action;

import org.anonymous.boot.test.annotation.Free;
import org.anonymous.boot.test.model.DateModel;
import org.anonymous.boot.test.model.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/04
 */
@Free
@RestController
public class DateAction {

	@PostMapping("date")
	Response<DateModel> date(@RequestBody DateModel dateModel) {
		Response<DateModel> resp = new Response<>();
		resp.setContent(dateModel);
		resp.setMsg("aaaa");
		return resp;
	}

}
