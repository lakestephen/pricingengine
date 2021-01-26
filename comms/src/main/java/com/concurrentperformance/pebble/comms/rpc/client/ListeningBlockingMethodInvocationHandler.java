package com.concurrentperformance.pebble.comms.rpc.client;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.concurrentperformance.pebble.comms.common.connection.Connection;
import com.concurrentperformance.pebble.comms.common.connection.ConnectionListener;
import com.concurrentperformance.pebble.comms.rpc.common.RpcIncommingServiceSupport;
import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.out.BlockingMethodInvocationHandler;

/**
 * An extension to the BlockingMethodInvocationHandler that will support 
 * the RpcClientServiceListenerSupport, prtoviding the ability to 
 * register(), deregister(), and then make sure that all registered 
 * listeners are notified of any calls over the listener interface IL. 
 *
 * @author Stephen Lake
 *
 * @param <IL> the listener interface
 */ 
public class ListeningBlockingMethodInvocationHandler<IL> extends BlockingMethodInvocationHandler 
		implements ConnectionListener {

	private final Class<IL> serviceS2CAPI;

	private Set<IL> incommingCallHandlerServices = new CopyOnWriteArraySet<IL>();
	
	private final static Byte byte_0 = new Byte((byte)0); 

	public ListeningBlockingMethodInvocationHandler(RpcConnection connection,
			String serviceIdentifier, Class<IL> serviceS2CAPI) {
		super(connection, serviceIdentifier);
		this.serviceS2CAPI = serviceS2CAPI;
		getConnection().register(this);
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if (method.getDeclaringClass() == RpcIncommingServiceSupport.class) {
			return handleRpcClientServiceListenerSupportMethods(proxy, method, args);
		}
		else if (method.getDeclaringClass() == serviceS2CAPI ||
				 method.getDeclaringClass() == RpcClientC2SSupport.class) {
			if (incommingCallHandlerServices.size() == 1) {
				//We treat this as a service call rather than a listener call
				// TODO what we should do is to have a separate interface that does not support RpcIncommingServiceSupport
				return method.invoke(incommingCallHandlerServices.iterator().next(), args);
			}
			else {
				for (IL listener : incommingCallHandlerServices) {
					method.invoke(listener, args);
				}
				return byte_0;
			}
		}
		else {
			return super.invoke(proxy, method, args);
		}
	}
	
	@SuppressWarnings("unchecked")
	private Object handleRpcClientServiceListenerSupportMethods(Object proxy, Method method, Object[] args) {
		String methodName = method.getName();

		if (methodName.equals("register")) {
			proxyRegister((IL)args[0]);
			return byte_0;
		}
		else if (methodName.equals("deregister")) {
			proxyDeregister((IL)args[0]);
			return byte_0;
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	private void proxyRegister(IL listener) {
		incommingCallHandlerServices.add(listener);
	}
	
	private void proxyDeregister(IL listener) {
		incommingCallHandlerServices.remove(listener);
	}

	@Override
	public void connection_notifyStarted(Connection connection) {
		for (IL listener : incommingCallHandlerServices) {
			((RpcClientC2SSupport)listener).connectionSupport_connectionStarted();
		}		
	}

	@Override
	public void connection_notifyStopped(Connection connection, boolean expected) {
		for (IL listener : incommingCallHandlerServices) {
			((RpcClientC2SSupport)listener).connectionSupport_connectionStopped();
		}		
	}

	@Override
	public String toString() {
		return "ListeningBlockingMethodInvocationHandler [serviceAPIInterfaceListener="
				+ serviceS2CAPI
				+ ", listeners="
				+ incommingCallHandlerServices
				+ "]";
	}
}
