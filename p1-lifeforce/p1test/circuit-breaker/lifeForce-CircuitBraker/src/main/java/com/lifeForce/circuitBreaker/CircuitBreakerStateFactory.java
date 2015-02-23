/**
 * 
 */
package com.lifeForce.circuitBreaker;

/**
 * @author arun_malik
 *
 */
public class CircuitBreakerStateFactory implements StateFactory {

	public com.lifeForce.circuitBreaker.OpenState OpenState() {
		return new OpenState();
	}

	public com.lifeForce.circuitBreaker.ClosedState ClosedState() {
		return new  ClosedState();  
	}

	public com.lifeForce.circuitBreaker.HalfOpenState HalfOpenState() {
		return new HalfOpenState();
	}

}
