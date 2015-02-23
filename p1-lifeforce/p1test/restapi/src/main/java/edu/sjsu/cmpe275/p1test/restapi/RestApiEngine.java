package edu.sjsu.cmpe275.p1test.restapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Main Class for the Rest API
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class RestApiEngine {
	
	public static void main(String[] args) {
		SpringApplication.run(RestApiEngine.class, args);
	}
}
