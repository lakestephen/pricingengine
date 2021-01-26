package com.concurrentperformance.pebble.comms.pipeline.connection;

import java.io.IOException;


public interface PipelineReader {

	public String readString() throws IOException;

	public byte readByte() throws IOException;
	public int readInt() throws IOException;
	public long readLong() throws IOException;
}
