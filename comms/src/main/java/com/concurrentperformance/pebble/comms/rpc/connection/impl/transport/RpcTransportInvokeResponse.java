package com.concurrentperformance.pebble.comms.rpc.connection.impl.transport;


/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class RpcTransportInvokeResponse extends RpcTransportResponse {
	
	private static final long serialVersionUID = 1767714271404942384L;

	private final Object result;


	public RpcTransportInvokeResponse(RpcTransportInvokeRequest request, Object result) {
		super(request, null);
		this.result = result;
	}

	public RpcTransportInvokeResponse(RpcTransportInvokeRequest request, Throwable exception) {
		super(request, exception);
		this.result = null;
	}


	public Object getResult() {
		return result;
	}

	@Override
	public String toString() {
		return "I<<Resp " + getRequestId() + ", " + result + ", exception=" + getException();
	}

}
