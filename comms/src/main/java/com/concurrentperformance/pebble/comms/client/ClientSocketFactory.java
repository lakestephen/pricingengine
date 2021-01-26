package com.concurrentperformance.pebble.comms.client;

import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;

import com.concurrentperformance.pebble.comms.client.impl.ConnectHelper;

/**
 * A Spring factory that will create a new singleton socket to a defined server.
 * 
 * @author Stephen Lake
 */
public class ClientSocketFactory implements FactoryBean<Socket> {

	private final Log log = LogFactory.getLog(this.getClass());

	
	private String serverHost;
	private int serverPort;
	private Socket socket;
	

	@Override
	public synchronized Socket getObject() throws Exception {
		if (socket == null ) {
			socket = ConnectHelper.startSocketToServer(serverHost, serverPort);
		}
		return socket;
	}

	@Override
	public Class<?> getObjectType() {
		return Socket.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	
	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
}