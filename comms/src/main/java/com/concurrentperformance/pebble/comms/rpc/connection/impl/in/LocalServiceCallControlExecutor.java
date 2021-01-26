package com.concurrentperformance.pebble.comms.rpc.connection.impl.in;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.common.connection.exception.ConnectionException;
import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.out.AsynchRpcTransportOutputStream;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportControlRequest;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportControlRequest.ControlAction;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportControlResponse;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportControlResponse.ControlProperty;

public class LocalServiceCallControlExecutor {

	private final Log log = LogFactory.getLog(this.getClass());

	private final RpcConnection connection;
	private final AsynchRpcTransportOutputStream outputStream; //TODO do we need thsi?	

	public LocalServiceCallControlExecutor(RpcConnection connection, AsynchRpcTransportOutputStream outputStream) {
		this.connection = connection;
		this.outputStream = outputStream;
	}
	
	void handleTransportControlRequest(final RpcTransportControlRequest controlRequest) {
		log.debug("Recieved control [" + controlRequest + "]");
		ControlAction action = controlRequest.getControlAction();
		
		switch (action) {
		case HANDSHAKE:
			handleAsHandshake(controlRequest);
			break;
		case IMMINENT_STOP:
			handleAsImminentStop(controlRequest);
			break;
		default:
			throw new ConnectionException("Unhandled control message [" + action + "] for [" + connection +"]");
		}
	}


	private void handleAsHandshake(RpcTransportControlRequest handshakeRequest) {
		log.debug("Handshake request recieved for [" + connection +"]");
		Map<ControlProperty, Object> controlProperties = connection.getHandshakeProps();
		RpcTransportControlResponse handshakeResponse = new RpcTransportControlResponse(handshakeRequest, controlProperties);
		log.debug("Responding to handshake request with connection details [" + controlProperties + "] for [" + connection +"]");
		outputStream.writeRpcTransport(handshakeResponse);		
	}

	private void handleAsImminentStop(RpcTransportControlRequest stopRequest) {
		log.info("Imminent stop notification recieved for [" + connection +"]");
		connection.imminentStop();

		Map<ControlProperty, Object> controlProperties = Collections.emptyMap();
		RpcTransportControlResponse handshakeResponse = new RpcTransportControlResponse(stopRequest, controlProperties );
		outputStream.writeRpcTransport(handshakeResponse);		
	}
	
	public void start(String threadBaseName) {
		// Not used at present
	}
	
	void stop() {
		log.debug("LocalServiceCallControlExecutor stopping for [" + connection + "]"); 
	}

}
