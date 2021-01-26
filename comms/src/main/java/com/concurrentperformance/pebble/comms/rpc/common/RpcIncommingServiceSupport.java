package com.concurrentperformance.pebble.comms.rpc.common;


/**
 * Services that support listener across the RPC socket
 * must support.   
 *
 * @author Stephen Lake
 */
public interface RpcIncommingServiceSupport<INAPI> {
	
	public void register(INAPI listener);

	public void deregister(INAPI listener); 
}
