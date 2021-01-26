package com.concurrentperformance.pebble.comms.rpc.server.socketlistener;

import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.common.connection.ConnectionListener;
import com.concurrentperformance.pebble.comms.common.server.socketlistener.impl.SkelitalServerConnectionFactory;
import com.concurrentperformance.pebble.comms.rpc.connection.IncommingServiceDefinition;
import com.concurrentperformance.pebble.comms.rpc.connection.OutgoingServiceDefinition;
import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.DefaultRpcConnection;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.out.BlockingMethodInvocationHandler;
import com.concurrentperformance.pebble.comms.rpc.server.service.RpcServerDuplexServiceDefinition;
import com.concurrentperformance.pebble.comms.rpc.server.service.RpcServerS2CSupport;
import com.concurrentperformance.pebble.comms.server.AsynchHandOffSocketHandler;

/**
 * Starts a ServerSocket listening on the passed port, accepting new socket
 * connections, handling by creating a DefaultRpcConnection, along with a new
 * instance of each of its defined services.
 * 
 * @author Stephen Lake
 */

public class RpcServerConnectionFactory extends SkelitalServerConnectionFactory implements
		AsynchHandOffSocketHandler, ConnectionListener {

	private final Log log = LogFactory.getLog(this.getClass());

	private Set<RpcServerDuplexServiceDefinition<? extends Object, ? extends Object>> duplexServices = 
			Collections.emptySet();

	@Override
	protected RpcConnection buildConnection(Socket client) {
		DefaultRpcConnection connection = new DefaultRpcConnection();

		Set<IncommingServiceDefinition<? extends Object, ? extends Object>> incommingServices = new HashSet<IncommingServiceDefinition<? extends Object, ? extends Object>>();
		Set<OutgoingServiceDefinition<? extends Object, ? extends Object>> outgoingServices = new HashSet<OutgoingServiceDefinition<? extends Object, ? extends Object>>();

		for (RpcServerDuplexServiceDefinition<? extends Object, ? extends Object> duplexService : duplexServices) {

			// create a new instance of the service for this connection
			RpcServerS2CSupport incommingC2Sservice = duplexService.createC2SService();

			// OUTGOING:create a proxy for the outgoing service
			BlockingMethodInvocationHandler outgoingS2CServiceHandler = new BlockingMethodInvocationHandler(
										connection, 
										duplexService.getServiceOutgoingS2CAPIIdentifier());
			Object outgoingS2CServiceProxy = Proxy.newProxyInstance(duplexService.getServiceOutgoingS2CAPI().getClassLoader(),
										new Class[] { duplexService.getServiceOutgoingS2CAPI() },
										outgoingS2CServiceHandler);
			
			OutgoingServiceDefinition outgoingService = new OutgoingServiceDefinition<>();
			outgoingService.setOutgoingService(outgoingS2CServiceProxy);
			outgoingService.setOutgoingServiceAPIInterface(duplexService.getServiceOutgoingS2CAPI());
			outgoingServices.add(outgoingService);

			// WIRE outgoing to incomming 
			incommingC2Sservice.setOutgoingS2CService(outgoingS2CServiceProxy);
			incommingC2Sservice.setConnection(connection);

			// INCOMMING: let the RPC Connection know about it.
			Class<? extends Object> incommingC2SAPI = duplexService.getServiceIncommingC2SAPI();
			IncommingServiceDefinition incommingServiceDefinition = new IncommingServiceDefinition();
			incommingServiceDefinition.setIncommingService(incommingC2Sservice);
			incommingServiceDefinition.setIncommingServiceInterface(incommingC2SAPI);
			incommingServices.add(incommingServiceDefinition);

			// Register the services with the connection
			if (incommingC2Sservice instanceof ConnectionListener) {
				connection.register((ConnectionListener) incommingC2Sservice);
			}
		}
		connection.addIncommingServiceDefinitions(incommingServices);
		connection.addOutgoingServiceDefinitions(outgoingServices);

		connection.setWeAreA(weAreA);
		connection.setWeAreConnectingTo("(" + weAreListeningFor + ")");
		connection.setSocket(client);

		return connection;
	}

	public final void setDuplexPrototypeServices(Set<RpcServerDuplexServiceDefinition<? extends Object, ? extends Object>> duplexServices) {
		this.duplexServices = duplexServices;
	}

	@Override
	public String getServerThreadName() {
		return "RpcListener:" + weAreListeningFor + ":" + port;
	}
}