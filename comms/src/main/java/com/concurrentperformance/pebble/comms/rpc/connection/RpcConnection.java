package com.concurrentperformance.pebble.comms.rpc.connection;

import java.util.Map;

import com.concurrentperformance.pebble.comms.common.connection.Connection;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportControlResponse.ControlProperty;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportInvokeRequest;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportInvokeResponse;

public interface RpcConnection extends Connection {

	public RpcTransportInvokeResponse transportRequestSynchronous(RpcTransportInvokeRequest request);
	
	public Map<ControlProperty, Object> getHandshakeProps();
	
	public void addIncommingServiceDefinition(IncommingServiceDefinition<? extends Object, ? extends Object> implementedService);
	
	public <T> T getService(Class<T> type);

}
