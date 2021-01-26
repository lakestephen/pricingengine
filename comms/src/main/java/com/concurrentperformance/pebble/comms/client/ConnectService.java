package com.concurrentperformance.pebble.comms.client;

import com.concurrentperformance.pebble.comms.common.connection.ConnectionListener;


/**
 * For use when you want control from the client end of starting and stopping 
 * the connection to the server. If you want an automatic connection, then 
 * use the ClientSocketFactory to connect and then inject the socket into the
 * connection. 
 * 
 * @author Stephen Lake
 *
 */
public interface ConnectService {

	boolean start();
	
	public void register(ConnectionListener listener); 
	public void deregister(ConnectionListener listener);

}
