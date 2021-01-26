package com.concurrentperformance.pebble.comms.client.impl;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.client.ConnectService;
import com.concurrentperformance.pebble.comms.common.connection.Connection;
import com.concurrentperformance.pebble.comms.common.connection.ConnectionListener;

public class DefaultConnectService implements ConnectService {

	private final Log log = LogFactory.getLog(this.getClass());

	private String serverHost;
	private int serverPort;
	private Connection connection;
	
	private static final int MAX_TRYS = 5; /// TODO have a retry strategy with backoff  
	private static final long TRY_PAUSE_MS = TimeUnit.MILLISECONDS.convert(5L, TimeUnit.SECONDS); ; /// TODO have a retry strategy with backoff  

	
	@Override
	public boolean start()  {
		
		for (int i=0;i<MAX_TRYS;i++) {
			boolean lastTry = (i == MAX_TRYS -1);
			try {
				attemptConnect();
				return true;
			} catch (IOException e) {
				String msg = "Connect attempt [" + (i+1) + "] failed. " + "Cause [" + e.getMessage() + "]";
				if (!lastTry) {
					msg += "Retry in [" + TRY_PAUSE_MS + "ms]. ";
				}
				log.info(msg);
			}
			
			if (!lastTry) {
				try {
					
					Thread.sleep(TRY_PAUSE_MS);
				} catch (InterruptedException e) {
					// SJL Auto-generated catch block
					log.error("SJL ", e);
				}
			}
		}
		
		return false;
	}

	private void attemptConnect() throws IOException {
		Socket socketToServer = ConnectHelper.startSocketToServer(serverHost, serverPort);
		connection.setSocket(socketToServer);
		connection.start();
	} 

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}
	
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void register(ConnectionListener listener) {
		//Delegate to the connection
		connection.register(listener);		
	}

	@Override
	public void deregister(ConnectionListener listener) {
		//Delegate to the connection
		connection.deregister(listener);		
	}
}
