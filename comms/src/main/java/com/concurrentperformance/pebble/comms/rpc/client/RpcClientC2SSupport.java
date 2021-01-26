package com.concurrentperformance.pebble.comms.rpc.client;

/**
 * Provides support of the client side of the connection for 
 * notifying of connection starts and stops. 
 * 
 * @author Stephen Lake
 *
 */
public interface RpcClientC2SSupport {

	void connectionSupport_connectionStarted();

	void connectionSupport_connectionStopped();

}
