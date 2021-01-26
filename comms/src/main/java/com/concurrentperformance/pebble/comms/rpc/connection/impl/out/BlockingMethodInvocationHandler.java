package com.concurrentperformance.pebble.comms.rpc.connection.impl.out;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.concurrentperformance.pebble.comms.common.connection.Connection;
import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportInvokeRequest;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportInvokeResponse;

/** 
 * An invocation handler for standard proxies that converts a method call on 
 * the interface proxy into a transport request and pass it to the transport 
 * layer.
 *  
 * The method call is asynchronous across the transport layer, but looks synchronous 
 * to the caller. 
 * 
 * @author Stephen Lake
 */
public class BlockingMethodInvocationHandler implements InvocationHandler { 
	
	private RpcConnection connection;
	private String outgoingServiceAPIIdentifier;

	private final static Byte byte_0 = Byte.valueOf((byte)0); 
	
	public BlockingMethodInvocationHandler(RpcConnection connection, String outgoingServiceAPIIdentifier) {
		this.connection = connection;
		this.outgoingServiceAPIIdentifier = outgoingServiceAPIIdentifier;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		
		if (method.getDeclaringClass() == Object.class) {
			Object response = handleObjectMethods(proxy, method, args);
			return response;
		}
	
		// create the transport object
		RpcTransportInvokeRequest request = new RpcTransportInvokeRequest(outgoingServiceAPIIdentifier, method, args);

		// transport the request, and get the response. This call will block
		RpcTransportInvokeResponse response = connection.transportRequestSynchronous(request);
		
		// process the response.
		if (response.getException() != null) {
			throw response.getException();
		}		
		return response.getResult();
	}

	private Object handleObjectMethods(Object proxy, Method method, Object[] args) {
		String methodName = method.getName();
		if (methodName.equals("hashCode")) {
			return proxyHashCode(proxy);
		}
		else if (methodName.equals("equals")) {
			return proxyEquals(proxy, args[0]);
		}
		else if (methodName.equals("toString")) {
			return proxyToString(proxy);
		}
		else {
			return byte_0;
		}
	}

	private Object proxyHashCode(Object proxy) {
		return Integer.valueOf(System.identityHashCode(proxy));
	}

	private Object proxyEquals(Object proxy, Object other) {
		return (proxy == other) ? Boolean.TRUE : Boolean.FALSE;
	}

	private Object proxyToString(Object proxy) {
		return proxy.getClass().getName() + "@" + Integer.toHexString(proxy.hashCode());
	}
	
	protected Connection getConnection() {
		return connection;
	}

}