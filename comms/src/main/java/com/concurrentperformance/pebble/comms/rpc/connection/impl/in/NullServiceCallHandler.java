package com.concurrentperformance.pebble.comms.rpc.connection.impl.in;

import java.io.IOException;

import com.concurrentperformance.pebble.comms.common.connection.exception.ConnectionException;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportInvokeRequest;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportInvokeResponse;

/**
 * Default service call handler for when non can be found. Simply returns 
 * an exception 
 * @author Stephen Lake
 *
 * @param <I>
 */
class NullServiceCallHandler<I> implements ServiceCallHandler<I> {
	
	@Override
	public RpcTransportInvokeResponse invoke(RpcTransportInvokeRequest invokeRequest)
			throws IllegalArgumentException, IllegalAccessException, IOException {
		String msg = "No service handler found for [" + invokeRequest + "].";
		Exception exception = new ConnectionException(msg);
		RpcTransportInvokeResponse response = new RpcTransportInvokeResponse(invokeRequest, exception);
		return response;
	}
}
