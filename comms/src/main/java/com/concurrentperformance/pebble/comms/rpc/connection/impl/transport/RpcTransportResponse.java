package com.concurrentperformance.pebble.comms.rpc.connection.impl.transport;


public abstract class RpcTransportResponse implements RpcTransport {

	private static final long serialVersionUID = 351548547998415072L;
	private final Long requestId; 
	private final Throwable exception;

	
	public RpcTransportResponse(RpcTransportRequest request, Throwable exception) {
		this.requestId = request.getRequestId();
		this.exception = exception;		
	}
	
	@Override
	public Long getRequestId() {
		return requestId;
	}
	
	public Throwable getException() {
		return exception;
	}

}
