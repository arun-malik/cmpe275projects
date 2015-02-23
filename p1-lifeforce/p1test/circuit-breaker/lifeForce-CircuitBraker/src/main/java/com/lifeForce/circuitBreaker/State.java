/**
 * 
 */
package com.lifeForce.circuitBreaker;

import com.lifeForce.circuitBreaker.util.CircuitBreakerException;

/**
 * @author arun_malik
 *
 */
public interface State {
	void preInvoke(CircuitBreaker circuitBreaker) throws CircuitBreakerException;
	void postInvoke(CircuitBreaker circuitBreaker);
	void onError(CircuitBreaker circuitBreaker, Throwable throwable);
}
