package com.concurrentperformance.pebble.comms.rpc.server.service;

import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;

/**
 * Provide services with the boiler plate code to support an
 * embedded listener. 
 * 
 * @author Stephen LAke
 *
 * @param <IL>
 */
public abstract class SkelitalRpcServerS2CSupport<IL> 
		implements RpcServerS2CSupport<IL>{

	private RpcConnection connection;
	private IL serviceListener; 
	
	@Override
	public void setOutgoingS2CService(IL serviceListener) {  
		this.serviceListener = serviceListener;
	}

	@Override
	public IL getOutgoingS2CService() { 
		return serviceListener;
	}
	
	@Override
	public void setConnection(RpcConnection connection) {
		this.connection = connection;
	}

	@Override
	public RpcConnection getConnection() {
		return connection;
	}
}
