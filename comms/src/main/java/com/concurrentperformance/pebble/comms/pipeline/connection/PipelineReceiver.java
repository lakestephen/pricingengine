package com.concurrentperformance.pebble.comms.pipeline.connection;

public interface PipelineReceiver<T> {

	void receive(T item);
}
