package com.lifeForce.circuitBreaker;

import com.lifeForce.circuitBreaker.util.CircuitBreakerException;

public interface Command {

	public void execute() throws CircuitBreakerException;
}
