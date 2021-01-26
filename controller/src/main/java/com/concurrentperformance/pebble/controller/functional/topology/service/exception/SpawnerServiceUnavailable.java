package com.concurrentperformance.pebble.controller.functional.topology.service.exception;

public class SpawnerServiceUnavailable extends TopologyMaintainanceException {

	private static final long serialVersionUID = 2746993678505954620L;

	public SpawnerServiceUnavailable(String message) {
    	super(message);
    }
    
    public SpawnerServiceUnavailable(Throwable cause) {
        super(cause);
    }
}
