package com.lifeForce.circuitBreaker;

import com.lifeForce.circuitBreaker.util.CircuitBreakerException;
import com.lifeForce.circuitBreaker.util.CircuitBreakerOpenException;
import com.lifeForce.circuitBreaker.util.CircuitBreakerProperties;


/**
 * @author arun_malik
 *
 */
public class OpenState implements State  {

    long tripTime;
    long openStateTimeMillis;
    
    public OpenState(){
    	this.openStateTimeMillis = System.currentTimeMillis();
    }

    public void preInvoke(CircuitBreaker circuitBreaker) throws CircuitBreakerException {
        long elapsedTime = System.currentTimeMillis() - tripTime;

        if (elapsedTime > CircuitBreakerProperties.getTimeoutMillis()) {
            circuitBreaker.attemptReset();
        } else {
            throw new CircuitBreakerOpenException("CircuitBreaker Open Exception - CircuitBreaker is in open state and requests are failing");
        }
    }

    public void postInvoke(CircuitBreaker circuitBreaker) {
    }

    public void onError(CircuitBreaker circuitBreaker, Throwable throwable) {

    }

    public void trip() {
        tripTime = System.currentTimeMillis();
    }

	/**
	 * @return the openStateTimeMillis
	 */
	public long getOpenStateTimeMillis() {
		return openStateTimeMillis;
	}

	
}
