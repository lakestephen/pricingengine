package com.concurrentperformance.pebble.comms.rpc.connection.impl.in;

import java.io.IOException;

import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportInvokeRequest;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportInvokeResponse;

public interface ServiceCallHandler<I> {

	public RpcTransportInvokeResponse invoke(RpcTransportInvokeRequest invokeRequest) 
			throws IllegalArgumentException, IllegalAccessException, IOException;

}
