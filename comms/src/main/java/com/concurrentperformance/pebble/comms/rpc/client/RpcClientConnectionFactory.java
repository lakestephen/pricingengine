package com.concurrentperformance.pebble.comms.rpc.client;

import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.concurrentperformance.pebble.comms.common.client.SkelitalClientConnectionFactory;
import com.concurrentperformance.pebble.comms.common.connection.ConnectionListener;
import com.concurrentperformance.pebble.comms.rpc.connection.IncommingServiceDefinition;
import com.concurrentperformance.pebble.comms.rpc.connection.OutgoingServiceDefinition;
import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.DefaultRpcConnection;


//TODO this should be common code with the server version
//TODO this should replace the single service construction with wrappers . 
public class RpcClientConnectionFactory extends SkelitalClientConnectionFactory 
	implements ConnectionListener {

	private Set<RpcClientDuplexServiceDefinition<? extends Object, ? extends Object, ? extends Object, ? extends Object>> duplexServices = 
			Collections.emptySet();

	@Override
	protected RpcConnection buildConnection(Socket client) {
		DefaultRpcConnection connection = new DefaultRpcConnection();

		Set<IncommingServiceDefinition<? extends Object, ? extends Object>> incommingServices = new HashSet<IncommingServiceDefinition<? extends Object, ? extends Object>>();
		Set<OutgoingServiceDefinition<? extends Object, ? extends Object>> outgoingServices = new HashSet<OutgoingServiceDefinition<? extends Object, ? extends Object>>();

		for (RpcClientDuplexServiceDefinition<? extends Object, ? extends Object, ? extends Object, ? extends Object> duplexService : duplexServices) {
			Object proxy = duplexService.buildProxy(connection);
			
			OutgoingServiceDefinition outgoingService = new OutgoingServiceDefinition<>();
			outgoingService.setOutgoingService(proxy);
			outgoingService.setOutgoingServiceAPIInterface(duplexService.getServiceOutgoingC2S());
			outgoingServices.add(outgoingService);			

			IncommingServiceDefinition<? extends Object, ? extends Object> incommingServiceDefinition =
					duplexService.buildIncommingServiceDefinition(proxy);
			incommingServices.add(incommingServiceDefinition);
		}
		
		connection.addIncommingServiceDefinitions(incommingServices);
		connection.addOutgoingServiceDefinitions(outgoingServices);

		connection.setWeAreA(weAreA);
		connection.setWeAreConnectingTo("(" + weAreConnectingTo + ")");
		connection.setSocket(client);

		return connection;
	}

	public final void setDuplexPrototypeServices(Set<RpcClientDuplexServiceDefinition<? extends Object, ? extends Object, ? extends Object, ? extends Object>> duplexServices) {
		this.duplexServices = duplexServices;
	}

}
