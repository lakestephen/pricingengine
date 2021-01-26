package com.concurrentperformance.pebble.comms.common.client;

import java.io.IOException;
import java.net.Socket;

import com.concurrentperformance.pebble.comms.client.impl.ConnectHelper;
import com.concurrentperformance.pebble.comms.common.connection.Connection;
import com.concurrentperformance.pebble.comms.common.connection.impl.SkelitalConnectionFactory;

/**
 * The connection factory is only used client side when the connection 
 * topology is not known at compile time. 
 * e.g. the connection from the controller to the spawner service where 
 * there are many spawners, depending on the number of machines that 
 * are in the cluster. 
 * 
 * Where the connection topology is known, but the listener might not be
 * in place, use the ConnectService.java
 * 
 * Where he connection is known, and the listener will be in place, create 
 * a connection directly. 
 * 
 * @author Stephen Lake
 *
 */
public abstract class SkelitalClientConnectionFactory extends SkelitalConnectionFactory {

	protected String weAreConnectingTo;

	public void setWeAreConnectingTo(String weAreConnectingTo) {
		this.weAreConnectingTo = weAreConnectingTo;
	}
	

	public Connection createAndStartNewConnection(String serverHost, int serverPort) throws IOException {
		Socket socket = ConnectHelper.startSocketToServer(serverHost, serverPort);
		Connection connection = buildConnection(socket);
		connection.start();
		return connection;
	}
}
