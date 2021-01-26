package com.concurrentperformance.pebble.comms.rpc.connection.impl.transport;



/**
 * Helper class that contains all the info needed for to invoke a remote method.
 * 
 * @author Stephen lake
 */
public class RpcTransportControlRequest extends RpcTransportRequest {
	
	private static final long serialVersionUID = -7394419617441942808L;

	public enum ControlAction {
		HANDSHAKE,
		IMMINENT_STOP,
	}

	private final ControlAction controlAction;
	
	public RpcTransportControlRequest(ControlAction controlAction) {
		this.controlAction = controlAction;
	}
	
	public ControlAction getControlAction() {
		return controlAction;
	}

	@Override
	public String toString() {
		return "C>>Requ " + getRequestId() + ", " + controlAction ;
	}
}