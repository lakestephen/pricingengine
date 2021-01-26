package com.concurrentperformance.pebble.comms.rpc.connection.impl.transport;

import java.io.Serializable;

public interface RpcTransport extends Serializable {
	
	public Long getRequestId();
}
