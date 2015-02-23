package com.lifeForce.circuitBreaker;

import com.lifeForce.circuitBreaker.util.CircuitBreakerException;


/**
 * @author arun_malik
 *
 */
public class HalfOpenState implements State  {

	public void preInvoke(CircuitBreaker circuitBreaker) throws CircuitBreakerException {
    }

    public void postInvoke(CircuitBreaker circuitBreaker) {
        circuitBreaker.reset();
    }

    public void onError(CircuitBreaker circuitBreaker, Throwable throwable) {
        circuitBreaker.trip();
    }
}
