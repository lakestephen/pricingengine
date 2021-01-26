package com.concurrentperformance.pebble.comms.rpc.connection.impl.transport;

import java.util.Map;

public class RpcTransportControlResponse extends RpcTransportResponse {

	private static final long serialVersionUID = 5446676903745556761L;

	public enum ControlProperty {
		NAME,
		ID,
	}
	
	private final Map<ControlProperty, Object> controlProperties;
	
	
	public RpcTransportControlResponse(RpcTransportControlRequest request, Map<ControlProperty, Object> controlProperties) {
		super(request, null);
		this.controlProperties = controlProperties;
	}
	
	public RpcTransportControlResponse(RpcTransportControlRequest request, Throwable exception) {
		super(request, exception);
		this.controlProperties = null;
	}
	
	public Object getControlProperty(ControlProperty property) {
		Object value = null;
		if (controlProperties != null) {
			value = controlProperties.get(property);
		}
		return value;
	}
	
	@Override
	public String toString() {
		return "C<<Resp " + getRequestId() + ", " + controlProperties ;
	}
}
