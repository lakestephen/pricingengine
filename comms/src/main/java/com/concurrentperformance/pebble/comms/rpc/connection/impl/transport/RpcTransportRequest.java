package com.concurrentperformance.pebble.comms.rpc.connection.impl.transport;


import java.util.concurrent.atomic.AtomicLong;

/**
 * Helper class that contains all the info needed for to invoke a remote method.
 * 
 * @author Stephen lake
 */
public abstract class RpcTransportRequest implements RpcTransport {

	private static final long serialVersionUID = -5478180768269139154L;

	private static final AtomicLong nextRequestId = new AtomicLong(0); //TODO separate into another generator class.

	private final Long requestId;
	
	// Convenience method of associating the response with the request. This saves 
	// placing it in a map, and then retrieving it again. 
	// TODO this is not volatile because the getters and setters are called while synchronized externally, by convention. Find a way of encapsulating this.  
	public RpcTransportResponse response;

	public RpcTransportRequest() {
		this.requestId = nextRequestId.incrementAndGet();
	}

	@Override
	public Long getRequestId() {
		return requestId;
	}

	public RpcTransportResponse getResponse() {
		return response;
	}

	public void setResponse(RpcTransportResponse response) {
		this.response = response;
	}
}