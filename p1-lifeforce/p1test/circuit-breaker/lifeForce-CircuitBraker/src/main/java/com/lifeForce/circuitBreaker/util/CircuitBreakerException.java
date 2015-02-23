/**
 * 
 */
package com.lifeForce.circuitBreaker.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author arun_malik
 *
 */
public class CircuitBreakerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID= 1527094918108669441L;

	private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerException.class); 
	
	public CircuitBreakerException(Throwable cause)
    {
		super(cause);
		
		logger.error(cause.getMessage()); 
		cause.printStackTrace();
        
    }

    public CircuitBreakerException(String message, Throwable cause)
    {
        super(message, cause);
        logger.error(cause.getMessage()); 
        logger.error(message); 
		cause.printStackTrace();
    }

    public CircuitBreakerException(String message)
    {
        super(message);
        logger.error(message);  
    }

    public CircuitBreakerException()
    {
        super(); 
    }
}
