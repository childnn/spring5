package org.anonymous.boot.test.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/04/24
 */
@Component
public class JsonResponse implements WebMvcConfigurer {

	/**
	 * @param converters initially an empty list of converters
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport#getMessageConverters()
	 */
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		MappingJackson2HttpMessageConverter jsonResp = new MappingJackson2HttpMessageConverter(objectMapper);
		// jsonResp.setJsonPrefix("111");

		converters.add(0, jsonResp);
	}
}
