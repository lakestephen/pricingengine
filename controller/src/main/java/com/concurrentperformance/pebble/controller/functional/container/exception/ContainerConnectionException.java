package com.concurrentperformance.pebble.controller.functional.container.exception;

public class ContainerConnectionException  extends Exception {

	public ContainerConnectionException(String message) {
    	super(message);
    }
    
    public ContainerConnectionException(Throwable cause) {
        super(cause);
    }
}
