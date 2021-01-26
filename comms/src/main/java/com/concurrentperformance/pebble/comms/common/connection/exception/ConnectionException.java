package com.concurrentperformance.pebble.comms.common.connection.exception;

public class ConnectionException extends RuntimeException {

	private static final long serialVersionUID = -3117766771203125564L;

	public ConnectionException(String message) {
		super(message);
	}
	
	public ConnectionException(String message, Throwable cause) {
		super(message, cause);
	}	
}
