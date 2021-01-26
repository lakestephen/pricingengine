package com.concurrentperformance.pebble.controller.functional.topology.service.exception;

/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class TopologyServiceException extends Exception {

	private static final long serialVersionUID = -3171628593323547610L;

	public TopologyServiceException(String message) {
    	super(message);
    }
    
    public TopologyServiceException(Throwable cause) {
        super(cause);
    }
}
