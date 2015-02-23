package com.lifeForce.circuitBreaker;

public interface CircuitBreaker {

	Command doOperation(Command command) throws Exception;
    void trip();
    void attemptReset();
    void reset();
    
}
