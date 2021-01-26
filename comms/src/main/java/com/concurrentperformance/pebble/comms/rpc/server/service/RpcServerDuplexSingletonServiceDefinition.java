package com.concurrentperformance.pebble.comms.rpc.server.service;

import com.concurrentperformance.pebble.comms.rpc.common.RpcIncommingServiceSupport;


/**
 * Define a service that gives the same singleton service to each connection.
 * 
 * @author Stephen
 *
 * @param <INAPI>
 * @param <OUTAPI>
 */
public class RpcServerDuplexSingletonServiceDefinition<INAPI extends RpcServerS2CSupport & RpcIncommingServiceSupport<OUTAPI>,OUTAPI>
	extends RpcServerDuplexServiceDefinition<INAPI, OUTAPI> {

	private INAPI singletonC2SService;

	public INAPI createC2SService() {
		return singletonC2SService;
	}

	public void setSingletonC2SService(INAPI singletonC2SService) {
		this.singletonC2SService = singletonC2SService;
	}

}
