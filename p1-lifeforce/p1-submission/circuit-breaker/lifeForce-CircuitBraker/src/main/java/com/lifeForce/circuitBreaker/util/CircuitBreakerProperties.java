/**
 * 
 */
package com.lifeForce.circuitBreaker.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author arun_malik
 *
 */
public class CircuitBreakerProperties {

	private static Integer threshold;
	private static Integer timeoutMillis;
	private static Integer recoveryTime;
	
	public static void Initialize() throws CircuitBreakerException, IOException{
		
		Properties props = null;
		
		props = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();  
		InputStream stream = loader.getResourceAsStream(CircuitBreakerConstants.CURCUIT_BREAKER_FILE);
		if(stream !=null){
			
			props.load(stream);
			
		}else{
			throw new CircuitBreakerException("Cannot load / find  "+CircuitBreakerConstants.CURCUIT_BREAKER_FILE+" file.", new FileNotFoundException());
		}
		
		if((props.getProperty(CircuitBreakerConstants.RECOVERY_TIME) !=null)&&(!(props.getProperty(CircuitBreakerConstants.RECOVERY_TIME).isEmpty()))){
			CircuitBreakerProperties.setRecoveryTime(Integer.parseInt(props.getProperty(CircuitBreakerConstants.RECOVERY_TIME))); 
		}else {
			
			throw new CircuitBreakerException("Missing Property "+ CircuitBreakerConstants.RECOVERY_TIME+" in "+CircuitBreakerConstants.CURCUIT_BREAKER_FILE +"file.", new NullPointerException());
		}
		
		
		if((props.getProperty(CircuitBreakerConstants.THRESHOLD) !=null)&&(!(props.getProperty(CircuitBreakerConstants.THRESHOLD).isEmpty()))){
			CircuitBreakerProperties.setThreshold(Integer.parseInt(props.getProperty(CircuitBreakerConstants.THRESHOLD)));
		}else {
			
			throw new CircuitBreakerException("Missing Property "+ CircuitBreakerConstants.THRESHOLD+" in "+CircuitBreakerConstants.CURCUIT_BREAKER_FILE +"file.", new NullPointerException());
		}
		
		
		if((props.getProperty(CircuitBreakerConstants.TIMEOUT_MILLIS) !=null)&&(!(props.getProperty(CircuitBreakerConstants.TIMEOUT_MILLIS).isEmpty()))){
			CircuitBreakerProperties.setTimeoutMillis(Integer.parseInt(props.getProperty(CircuitBreakerConstants.TIMEOUT_MILLIS))); 
		}else {
			
			throw new CircuitBreakerException("Missing Property "+ CircuitBreakerConstants.TIMEOUT_MILLIS+" in "+CircuitBreakerConstants.CURCUIT_BREAKER_FILE +"file.", new NullPointerException());
		}
		
	}

	/**
	 * @return the threshold
	 */
	public static Integer getThreshold() {
		return threshold;
	}

	/**
	 * @param threshold the threshold to set
	 */
	public static void setThreshold(Integer threshold) {
		CircuitBreakerProperties.threshold = threshold;
	}

	/**
	 * @return the timeoutMillis
	 */
	public static Integer getTimeoutMillis() {
		return timeoutMillis;
	}

	/**
	 * @param timeoutMillis the timeoutMillis to set
	 */
	public static void setTimeoutMillis(Integer timeoutMillis) {
		CircuitBreakerProperties.timeoutMillis = timeoutMillis;
	}

	/**
	 * @return the recoveryTime
	 */
	public static Integer getRecoveryTime() {
		return recoveryTime;
	}

	/**
	 * @param recoveryTime the recoveryTime to set
	 */
	public static void setRecoveryTime(Integer recoveryTime) {
		CircuitBreakerProperties.recoveryTime = recoveryTime;
	}
	
}
