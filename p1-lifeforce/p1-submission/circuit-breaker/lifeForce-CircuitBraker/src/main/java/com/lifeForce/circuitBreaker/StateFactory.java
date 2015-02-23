package com.lifeForce.circuitBreaker;


/**
 * @author arun_malik
 *
 */
public interface StateFactory {


    public OpenState OpenState();

    public ClosedState ClosedState();

    public HalfOpenState HalfOpenState();
}
