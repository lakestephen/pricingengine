package com.concurrentperformance.pebble.controllerspawned.api;

public class SpawnedComponentControlException extends Exception {

	private static final long serialVersionUID = 6334102599565711532L;

	public SpawnedComponentControlException(String message) {
		super(message);
	}
	
	public SpawnedComponentControlException(String message, Throwable cause) {
		super(message, cause);
	}	
}
