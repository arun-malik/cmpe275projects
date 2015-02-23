package com.lifeForce.circuitBreaker;

import java.util.concurrent.atomic.AtomicInteger;

import com.lifeForce.circuitBreaker.util.CircuitBreakerProperties;


/**
 * @author arun_malik
 *
 */
public class ClosedState implements State  {

    AtomicInteger failureCount = new AtomicInteger(0);

    public void preInvoke(CircuitBreaker circuitBreaker) {
     
    }

    public void postInvoke(CircuitBreaker circuitBreaker) {
        resetFailureCount();
    }

    private void resetFailureCount() {
        failureCount.set(0);
    }

    public void onError(CircuitBreaker circuitBreaker, Throwable throwable) {
        int currentCount = failureCount.incrementAndGet();

        if (currentCount >= CircuitBreakerProperties.getThreshold()) {
            circuitBreaker.trip();
        }
    }
}
