package com.concurrentperformance.pebble.comms.pipeline.connection.impl;

import java.io.IOException;
import java.net.Socket;

import com.caucho.hessian.io.Hessian2Output;
import com.concurrentperformance.pebble.comms.common.connection.exception.ConnectionException;
import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineConnection;
import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineWriter;

public class HessianPipelineWriter implements PipelineWriter {

	private Hessian2Output out;
	
	private PipelineConnection connection;
	
	HessianPipelineWriter(PipelineConnection connection) {
		this.connection = connection;
	}

	public void start(Socket socket) throws IOException {
		this.out = new Hessian2Output(socket.getOutputStream());
	}
	
	@Override
	public void writeString(String value) throws IOException {
		checkConnection();
		out.writeString(value);
	}

	@Override
	public void writeByte(byte character) throws IOException {
		checkConnection();
		out.writeBytes(new byte[]{character});
	}
	
	@Override
	public void writeInt(int value) throws IOException, ConnectionException {
		checkConnection();
		out.writeInt(value);
	}

	@Override
	public void writeLong(long value) throws IOException, ConnectionException {
		checkConnection();
		out.writeLong(value);
	}

	@Override
	public void flush() throws IOException {
		checkConnection();
		out.flush();
	}

	private void checkConnection() {
		if (connection.isStoppedOrStopping()) {
			throw new ConnectionException("Connection Stopped or stopping");
		}
	}

	public void stop() {
		// TODO 		
	}

	public void setThreadName(String threadBaseName) {
		// Nothing to do		
	}

}
