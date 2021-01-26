package com.concurrentperformance.pebble.comms.rpc.server.service;

import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;


/**
 * Interface defining that a server side service is 
 * linked with a listener interface. Generally the 
 * service will make calls on the listener interface.
 * 
 * @author Stephen Lake 
 *
 * @param <IL> the listener interface. 
 */
public interface RpcServerS2CSupport<IL> { 

	public void setOutgoingS2CService(IL serviceListener);
	public IL getOutgoingS2CService();

	public void setConnection(RpcConnection connection);
	public RpcConnection getConnection();

}
