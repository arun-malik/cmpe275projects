/**
 * 
 */
package com.lifeForce.circuitBreaker;

import java.io.IOException;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import com.lifeForce.circuitBreaker.util.*;

/**
 * @author arun_malik
 *
 */
public class CircuitBreakerHandlerTest extends MockObjectTestCase {

	
	private static final int FAILURE_THRESHOLD = 10;
    CircuitBreakerHandler cbhTest;
    Command command;
    Mock mockCommand;
    


    public void setUp() throws CircuitBreakerException, IOException {
 
    	// mandatory to read values from properties file
        CircuitBreakerProperties.Initialize();
        
        CircuitBreakerStateFactory cbsFactory = new CircuitBreakerStateFactory();  
        cbhTest = new CircuitBreakerHandler();
        cbhTest.setStateFactory(cbsFactory);
        cbhTest.initialise();

        mockCommand = mock(Command.class);
        command = (Command) mockCommand.proxy();
    }

    public void testDefaultOperation() throws Exception {

        mockCommand.expects(once()).method("execute");
        cbhTest.doOperation(command);
    }

    public void testBreakerTripsAfterThresholdPassed() throws Exception {

        tripBreaker();

        boolean exception = false;

        try {
            cbhTest.doOperation(command);
        } catch (CircuitBreakerOpenException e) {
            exception=true;
        }

        assertTrue("CircuitBreaker Open Exception - CircuitBreaker is in open state and requests are failing",exception);
    }

    public void testBreakerResetsAfterTimeoutPassedWithSuccessfulCommand() throws Exception {

        tripBreaker();
     
        Thread.sleep(10000);

        mockCommand.expects(atLeastOnce()).method("execute");
        cbhTest.doOperation(command);

        cbhTest.doOperation(command);
    }

    public void testBreakerTripsAfterTimeoutPassedWithBadCommand() throws Exception {

        tripBreaker();
        
       Thread.sleep(10001);

        mockCommand.expects(atLeastOnce()).method("execute").
                will(onConsecutiveCalls(throwException(new CircuitBreakerException()),returnValue(null)));

        try {
            cbhTest.doOperation(command);
        } catch (Exception e) {
            
        }

         boolean exception = false;

        try {
            cbhTest.doOperation(command);
        } catch (CircuitBreakerException e) {
            exception=true;
        }

        assertTrue("Circuit breaker should have tripped and so should be failing fast",exception);
    }


    private void tripBreaker() {
    	CircuitBreakerException cbException = new CircuitBreakerException();

        Mock badCommand = mock(Command.class,"bad command");

        badCommand.expects(atLeastOnce()).method("execute").will(throwException(cbException));

        for (int i = 0; i < FAILURE_THRESHOLD; i++) {

            try {
                cbhTest.doOperation((Command)badCommand.proxy());
            } catch (Exception e) {
                assertEquals(cbException, e);
            }
        }
    }
}
