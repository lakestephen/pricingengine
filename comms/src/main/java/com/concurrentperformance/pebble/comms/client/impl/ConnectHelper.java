package com.concurrentperformance.pebble.comms.client.impl;

import java.io.IOException;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConnectHelper {

	private static final Log log = LogFactory.getLog(ConnectHelper.class);

	public static Socket startSocketToServer(String serverHost, int serverPort) throws IOException { 
		
		Socket socketToServer = null;
		// open a socket connection
		try {
			log.info("Opening new Socket to [" + serverHost + ":" + serverPort + "]");
			socketToServer = new Socket(serverHost, serverPort); //TODO confirm that we have the correct service type over the socket
			
		} catch (IOException e) {
			log.error("Error opening socket to [" + serverHost + ":" + serverPort + "] cause [" + e.getMessage() + "]" );
			throw e;
		}

		socketToServer.setTcpNoDelay(true);

		log.info("Started socket to server [" + socketToServer + "]");
		
		return socketToServer;
	}

}
