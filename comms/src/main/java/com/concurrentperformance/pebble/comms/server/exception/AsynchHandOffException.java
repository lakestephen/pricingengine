package com.concurrentperformance.pebble.comms.server.exception;

/**
 * Exceptions for the AsynchHandOffSocketServer.
 * 
 * @author Stephen Lake
 */
public class AsynchHandOffException extends RuntimeException {

	private static final long serialVersionUID = -3196862301624702715L;

	public AsynchHandOffException(String message) {
		super(message);
	}
	
	public AsynchHandOffException(String message, Throwable cause) {
		super(message, cause);
	}	
}
