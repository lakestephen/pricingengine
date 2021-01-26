package com.concurrentperformance.pebble.comms.pipeline.connection;

import java.io.IOException;

public interface PipelineWriter {

	public void writeString(String value) throws IOException;

	public void writeByte(byte value) throws IOException;
	public void writeInt(int value) throws IOException; 
	public void writeLong(long containerId) throws IOException; 
	
	public void flush() throws IOException;
}
