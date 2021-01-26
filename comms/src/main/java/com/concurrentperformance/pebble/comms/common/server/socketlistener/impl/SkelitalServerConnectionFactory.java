package com.concurrentperformance.pebble.comms.common.server.socketlistener.impl;

import java.io.IOException;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.common.connection.Connection;
import com.concurrentperformance.pebble.comms.common.connection.exception.ConnectionException;
import com.concurrentperformance.pebble.comms.common.connection.impl.SkelitalConnectionFactory;
import com.concurrentperformance.pebble.comms.server.AsynchHandOffSocketHandler;
import com.concurrentperformance.pebble.comms.server.AsynchHandOffSocketServer;

public abstract class SkelitalServerConnectionFactory extends SkelitalConnectionFactory  
	implements AsynchHandOffSocketHandler {

	private final Log log = LogFactory.getLog(this.getClass());

	private AsynchHandOffSocketServer server;

	protected String weAreListeningFor; 
	protected int port = -1;
	
	public void start() throws ConnectionException {
		// build and start the server that will listen on the port.
		if (port <= 0) {
			throw new ConnectionException("Port [" + port + "] must be set to non zero ");
		}
		try {
			server = new AsynchHandOffSocketServer(port);
			server.setHandler(this);
			server.startListening();
		}
		catch (RuntimeException e) {
			throw new ConnectionException("Problem starting AsynchHandOffSocketServer", e);
		}
	}
	
	@Override
	public void handOffNewSocketConnection(Socket client) { 
		Connection connection = buildConnection(client);
		
		try {
			connection.start();
		}
		catch (IOException e) {
			log.error("Problem starting connection", e);
		}
	}

	public void setWeAreListeningFor(String weAreListeningFor) {
		this.weAreListeningFor = weAreListeningFor;
	}
	
	public final void setPort(int port) {
		this.port = port;
	}
	
	@Override
	public String toString() {
		return "[" + weAreListeningFor + "] [" + this.getClass().getSimpleName() + "] connections on port [" + port + "]"; 
	}
}
