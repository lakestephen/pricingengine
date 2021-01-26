package com.concurrentperformance.pebble.comms.common.connection;

/**
 * Listener interface for notifications from a single specific connection. 
 * 
 * NOTE: If you want to hear notifications for a factory generating 
 * new connections, implement ConnectionFactoryListener, and register with 
 * either the PipelineConnectionFactory or RpcConnectionFactory
 *  
 * @author Stephen Lake
 */
public interface ConnectionListener {
	
	/**
	 * The specific connection has started. 
	 * The passed connection is the one that you registered with in 
	 * the first place, and is useful where you are registering with 
	 * several connections to be able to descriminate.  
	 * 
	 * @param connection
	 */
	void connection_notifyStarted(Connection connection);

	/**
	 * The specific connection has stopped. This can be expected, or 
	 * expected. 
	 * The passed connection is the one that you registered with in 
	 * the first place, and is useful where you are registering with 
	 * several connections to be able to descriminate.  
	 * 
	 * @param connection
	 * @param expected
	 */
	void connection_notifyStopped(Connection connection, boolean expected);
}
