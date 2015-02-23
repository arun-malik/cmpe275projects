/**
 * 
 */
package com.lifeForce.circuitBreaker;

/**
 * @author arun_malik
 *
 */
public class CircuitBreakerHandler implements CircuitBreaker{

	private StateFactory stateFactory;

    private State state;

    public Command doOperation(final Command command) throws Exception {

        try {
            getState().preInvoke(this);
            command.execute();
            getState().postInvoke(this);

        } catch (Exception e) {
            getState().onError(this, e);
            throw e;
        }
        return command;
    }

    private State getState() {
        return state;
    }


    public void trip() {
        OpenState openState = stateFactory.OpenState();
        openState.trip();
        this.state = openState;
    }


    public void attemptReset() {
        this.state = stateFactory.HalfOpenState();
    }


    public void reset() {
        this.state = stateFactory.ClosedState();
    }

    /**
     * Must be called before first use
     */
    public void initialise() {
        this.state = stateFactory.ClosedState();
    }

    public void setStateFactory(StateFactory stateFactory) {
        this.stateFactory = stateFactory;
    }
}
