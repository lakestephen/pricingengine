package com.concurrentperformance.pebble.controller.functional.topology.service.exception;

/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class TopologyMaintainanceException extends Exception {

	private static final long serialVersionUID = 7303185641982121703L;

	public TopologyMaintainanceException(String message) {
    	super(message);
    }
    
    public TopologyMaintainanceException(Throwable cause) {
        super(cause);
    }
}
