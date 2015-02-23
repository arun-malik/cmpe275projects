package edu.sjsu.cmpe275.p1test.restapi.service;

public class StandardApiException extends Exception {

	public StandardApiException() {
		super();
	}

	public StandardApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public StandardApiException(String message, Throwable cause) {
		super(message, cause);
	}

	public StandardApiException(String message) {
		super(message);
	}

	public StandardApiException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = -7794021159381321440L;
	
}
