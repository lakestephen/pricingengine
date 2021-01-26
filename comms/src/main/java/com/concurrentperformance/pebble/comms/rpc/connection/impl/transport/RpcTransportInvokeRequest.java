package com.concurrentperformance.pebble.comms.rpc.connection.impl.transport;


import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class that contains all the info needed for to invoke a remote method.
 * 
 * @author Stephen lake
 */
public class RpcTransportInvokeRequest extends RpcTransportRequest {

	private static final long serialVersionUID = -754626288897927253L;
	
	public final String serviceIdentifier;
	public final String methodSignatature;
	public final List<Object> args;

	public RpcTransportInvokeRequest(String serviceIdentifier, Method method, Object[] args) {
		this.serviceIdentifier = serviceIdentifier;
		this.methodSignatature = method.toGenericString();
		this.args = (args==null)? null:Arrays.asList(args);
	}
	
	/**
	 * @return the methodSignatature
	 */
	public final String getMethodSignatature() {
		return methodSignatature;
	}

	/**
	 * @return the args
	 */
	public final Object[] getArgs() {
		Object[] argsArray = null;
		
		if (args != null) {
			argsArray = args.toArray();
		}
		return argsArray;
	}

	/**
	 * @return the serviceIdentifier
	 */
	public final String getServiceIdentifier() {
		return serviceIdentifier;
	}
	
	@Override
	public String toString() {
		return "I>>Requ " + getRequestId() + ", " + serviceIdentifier
				+ ", " + methodSignatature + ", " + args ;
	}

}