package com.concurrentperformance.pebble.comms.pipeline.connection;

import com.concurrentperformance.pebble.comms.common.connection.Connection;

public interface PipelineConnection extends Connection {
	
	public PipelineWriter getWriter();
	
	public void imminentStop();

}
