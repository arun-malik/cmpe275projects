package edu.sjsu.cmpe275.p1test.restapi.ws;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import edu.sjsu.cmpe275.p1test.restapi.service.StandardApiException;

@ControllerAdvice
public class ApiErrorHandler {
	
	
	@ExceptionHandler({StandardApiException.class})
	public ResponseEntity<Map<String, String>> handleRandomTestError(StandardApiException ex, final HttpServletResponse response) {
		Map<String, String> exception = new HashMap<String, String>();
		exception.put("type", ex.getClass().getName());
		exception.put("msg", ex.getMessage());
		exception.put("isError", "true");
		
		// Enforce content type: application/json
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
		response.setContentType("application/json");
        
		return new ResponseEntity<Map<String,String>>(exception, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
