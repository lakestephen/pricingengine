package com.concurrentperformance.pebble.comms.rpc.connection.impl;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.common.connection.impl.SkelitalConnection;
import com.concurrentperformance.pebble.comms.rpc.connection.IncommingServiceDefinition;
import com.concurrentperformance.pebble.comms.rpc.connection.OutgoingServiceDefinition;
import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.in.LocalServiceCallControlExecutor;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.in.LocalServiceCallInvokeExecutor;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.in.RpcTransportInputStream;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.out.AsynchRpcTransportOutputStream;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.out.RemoteTransporter;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportControlRequest;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportControlRequest.ControlAction;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportControlResponse;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportControlResponse.ControlProperty;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportInvokeRequest;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportInvokeResponse;

/**
 * An orchestration class that takes a socket and manages all two way 
 * communication via proxied service calls. This class will sit on both ends of 
 * the socket and once established, both ends perform the same role.
 * 
 * @author Stephen
 *
 */
public class DefaultRpcConnection extends SkelitalConnection implements RpcConnection {

	private final Log log = LogFactory.getLog(this.getClass());

	private final AsynchRpcTransportOutputStream outputStream;
	private final RemoteTransporter remoteTransporter;
	private final LocalServiceCallInvokeExecutor serviceCallInvokeExecutor;
	private final LocalServiceCallControlExecutor serviceCallControlExecutor;
	private final RpcTransportInputStream inputStream;
	private final Map<Class<?>, Object> outgoingServices;
		

	public DefaultRpcConnection() {
		outputStream = new AsynchRpcTransportOutputStream(this);
		remoteTransporter = new RemoteTransporter(this, outputStream);
		serviceCallInvokeExecutor = new LocalServiceCallInvokeExecutor(this, outputStream);
		serviceCallControlExecutor = new LocalServiceCallControlExecutor(this, outputStream);
		inputStream = new RpcTransportInputStream(
				this, serviceCallInvokeExecutor, serviceCallControlExecutor, remoteTransporter);
		outgoingServices = new ConcurrentHashMap<Class<?>, Object>();
	}
	
	@Override
	protected void startComponents(Socket socket, String threadBaseName) throws IOException {
		outputStream.start(socket, threadBaseName);
		serviceCallInvokeExecutor.start(threadBaseName);		
		serviceCallControlExecutor.start(threadBaseName);
		inputStream.start(socket, threadBaseName);
	}

	@Override
	protected void performHandshake() {
		log.debug("Sending handshake [" + this + "]");
		RpcTransportControlRequest handshakeRequest = new RpcTransportControlRequest(ControlAction.HANDSHAKE);
		RpcTransportControlResponse response = remoteTransporter.transportRequestSynchronous(handshakeRequest);		
		log.debug("Got handshake response [" + response + "] for [" + this + "]");
		handshakeRecieved((String)response.getControlProperty(ControlProperty.NAME), 
						  (long)response.getControlProperty(ControlProperty.ID));
	}
	
	@Override
	protected void doUpdateThreadName(String threadBaseName) {
		outputStream.setThreadName(threadBaseName);
		serviceCallInvokeExecutor.setThreadName(threadBaseName);
		inputStream.setThreadName(threadBaseName);
	}
	
	@Override
	public Map<ControlProperty, Object> getHandshakeProps() {
		Map<ControlProperty, Object> controlProperties = new HashMap<ControlProperty, Object>();
		controlProperties.put(ControlProperty.NAME, getWeAreA()); 
		controlProperties.put(ControlProperty.ID, getWeAreAId()); 
		return controlProperties;
	}

	@Override
	protected void stopComponents() {
		remoteTransporter.stop();
		outputStream.stop();
		inputStream.stop();
		serviceCallInvokeExecutor.stop();
	}

	@Override
	protected void signalImminentStopToRemoteSocket() {
		log.debug("Sending stop from [" + this + "]");
		RpcTransportControlRequest stopRequest = new RpcTransportControlRequest(ControlAction.IMMINENT_STOP);
		RpcTransportControlResponse response = remoteTransporter.transportRequestSynchronous(stopRequest);		
		log.debug("Got stop response [" + response + "] for [" + this + "]");
	}

	/**
	 * This method is in the connection because at the time the proxies are 
	 * configured that need to make the transport calls, the connection
	 * is not initialised, and therefore remoteBlockingRequestHandler does not exist. 
	 */
	@Override
	public RpcTransportInvokeResponse transportRequestSynchronous(RpcTransportInvokeRequest request) {
		if (remoteTransporter == null) {
			throw new IllegalStateException("Can't transport request [" + this + "]. (Did you call start() ?)");
		}
		return remoteTransporter.transportRequestSynchronous(request);
	}	

	@Override
	public final void addIncommingServiceDefinition(IncommingServiceDefinition<? extends Object, ? extends Object> incommingServiceDefinition) {
		Set<IncommingServiceDefinition<? extends Object, ? extends Object>> incommingServiceDefinitions = new HashSet<IncommingServiceDefinition<? extends Object, ? extends Object>>();
		incommingServiceDefinitions.add(incommingServiceDefinition);
		addIncommingServiceDefinitions(incommingServiceDefinitions);
	}
	
	public final void addIncommingServiceDefinitions(Set<IncommingServiceDefinition<? extends Object, ? extends Object>> incommingServiceDefinitions) {
		serviceCallInvokeExecutor.setIncommingServicDefinitions(incommingServiceDefinitions);
	}

	public final void addOutgoingServiceDefinitions(Set<OutgoingServiceDefinition<? extends Object, ? extends Object>> outgoingServiceDefinitions) {
		for (OutgoingServiceDefinition<? extends Object, ? extends Object> outgoingServiceDefinition : outgoingServiceDefinitions) {
			outgoingServices.put(outgoingServiceDefinition.getOutgoingServiceAPIInterface(), outgoingServiceDefinition.getOutgoingService());
		}
	}

	public <OUT> OUT getService(Class<OUT> serviceInterface) {
		return (OUT)outgoingServices.get(serviceInterface);
	}
}
