package com.concurrentperformance.pebble.comms.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.server.exception.AsynchHandOffException;


/**
 * A socket based server that will listen on a port, and then hand 
 * off the new connection to the passed in AsynchHandOffSocketHandler
 * 
 * @author Stephen lake
 */
public class AsynchHandOffSocketServer {

	private final Log log = LogFactory.getLog(this.getClass());
	
	private final int port;
	private volatile ServerSocket listeningSocket;
	private volatile boolean stopped = false;
	private volatile AsynchHandOffSocketHandler handler;

	public AsynchHandOffSocketServer(int port) {
		this.port = port;
	}
	
	public void setHandler(AsynchHandOffSocketHandler handler) {
		if (handler == null) {
			throw new AsynchHandOffException("Handler cant be null.");
		}
		
		this.handler = handler;
	}

	public synchronized void startListening() throws AsynchHandOffException {
		if (listeningSocket != null) {
			throw new AsynchHandOffException("Already listening on [" + listeningSocket + "]");
		}
		
		if (handler == null) {
			throw new AsynchHandOffException("No AsynchronousHandOffHandler set");
		}		
			
		try {
			listeningSocket = new ServerSocket(port);
		} catch (IOException e) {
			throw new AsynchHandOffException("Could not bind to port [" + port + "]", e);
		}

		String threadName = handler.getServerThreadName();
		if (threadName == null) {
			throw new AsynchHandOffException("Handler [" + handler + "] returned a null thread name.");
		}

		log.info("Starting listening for " + handler );
		new Thread(new ListenerTask(), threadName).start();
	}

	public synchronized void stopListening() throws AsynchHandOffException {
		if (stopped) {
			throw new AsynchHandOffException("Already stopped on [" + listeningSocket + "]");
		}
		
		if (listeningSocket == null) {
			throw new AsynchHandOffException("Server never started listening on [" + port + "]");
		}
		
		try {
			listeningSocket.close();
		} catch (IOException e) {
			throw new AsynchHandOffException("Failed to close listening socket[" + listeningSocket + "]", e);
		}
		
		while (!listeningSocket.isClosed()) { //TODO use a condition queue. 
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				log.error("Intreuption while waiting for listening socket to close", e);
			}
		}
		
		stopped = true;
	}

	private class ListenerTask implements Runnable {
		
		public void run() {
			while (!stopped && !listeningSocket.isClosed()) { //TODO need to make more robust
				try {
					log.debug("Listening (again) [" + listeningSocket + "] for [" + handler + "]");
					Socket newConnection = listeningSocket.accept();
					
					log.info("Handing off new connection [" + newConnection + "] to [" + handler + "]");
					handler.handOffNewSocketConnection(newConnection);
					
				} catch (SocketException e) {
					log.info(e.getMessage(), e);
				} catch (IOException e) {
					log.error("Error accepting connection", e);
				}
			}
			
			log.info("ObjectServer listening socket closed.");
		}
	}
		
	public synchronized boolean isClosed() {
		boolean closed = true;
		
		if (!stopped && listeningSocket != null) {
			closed = listeningSocket.isClosed();
		}
		
		return closed; 		
	}
}