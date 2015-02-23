package edu.sjsu.cmpe275.p1test.restapi.config;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

	/**
	 * Configure upload limits
	 * @return
	 */
	@Bean
	MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize("58KB");
		factory.setMaxRequestSize("58KB");
		return factory.createMultipartConfig();
	}

}
