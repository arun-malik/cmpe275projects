/**
 * 
 */
package com.lifeForce.circuitBreaker.util;

/**
 * @author arun_malik
 *
 */
public class CircuitBreakerOpenException extends CircuitBreakerException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1677533205967200661L;

	public CircuitBreakerOpenException(String message) {
        super(message);
    }
}
