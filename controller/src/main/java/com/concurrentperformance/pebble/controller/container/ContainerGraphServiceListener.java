package com.concurrentperformance.pebble.controller.container;

public interface ContainerGraphServiceListener {

	void containerGraphService_graphItemCreated(long id, String path, String calculation, String outputEventId);
}
