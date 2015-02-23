package edu.sjsu.cmpe275.p1test.restapi.ws;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.sjsu.cmpe275.p1test.restapi.service.StandardApiException;

@RestController
public class ApiError {
	
	@RequestMapping(value = {"/api/error", "/api/exception"}, method=RequestMethod.GET, produces = "application/json")
	public Map<String, Object> testException() throws StandardApiException {
		throw new StandardApiException("This is SPARTA!");
	}
	
	@RequestMapping(value = {"/api/stderror", "/api/stdexception"}, method=RequestMethod.GET, produces = "application/json")
	public Map<String, Object> testStdException() throws Exception {
		throw new Exception("La La Land!!");
	}
}
