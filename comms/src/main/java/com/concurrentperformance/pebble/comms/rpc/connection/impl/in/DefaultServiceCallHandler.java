package com.concurrentperformance.pebble.comms.rpc.connection.impl.in;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportInvokeRequest;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportInvokeResponse;

class DefaultServiceCallHandler<I> implements ServiceCallHandler<I> {
	
	private final Log log = LogFactory.getLog(this.getClass());

	private final I service;
	private final Class<I> serviceInterface;	
	private final String serviceIdentifier;
	
	/**
	 * Map of service interface Class.toGenericString() to the service Method 
	 */
	private Map<String, Method> serviceMethods;

	public DefaultServiceCallHandler(I service, Class<I> serviceInterface, String serviceIdentifier) {
		super();
		this.service = service;
		this.serviceInterface = serviceInterface;
		this.serviceIdentifier = serviceIdentifier;
		this.serviceMethods = mineServiceMethods(serviceInterface, service);
	} 
	
	@Override
	public RpcTransportInvokeResponse invoke(RpcTransportInvokeRequest request) throws IllegalArgumentException, IllegalAccessException, IOException {
		
		Object[] args = request.getArgs();
		String methodSignature = request.getMethodSignatature();
		
		// find the method to call
		Method method = getMethodForSignature(methodSignature);
		
		//call the method and assemble the response. 
		RpcTransportInvokeResponse response = null; 
		
		if (method != null) {
		
			try {
				Object result = method.invoke(service, args);
				response = new RpcTransportInvokeResponse(request, result);	
			} catch (InvocationTargetException e) {
				Throwable cause = e.getCause();
				//TODO how do we prevent exceptions from parts of the application that are not available remotely being passed over the wire, leading to Clas not found exception. 
				// TODO perhaps just send a summary of the exception, and log locally?			
				log.warn("Exception [" + cause.toString() + "] while processing request [" + request +"] cause [" + cause.getMessage() + "]", e.getCause());
				response = new RpcTransportInvokeResponse(request, cause);	
			}
		} else {
			String msg = "Cant find method [" + methodSignature +"], methods available [" + serviceMethods + "], for identifier [" + serviceIdentifier + "]";
			RuntimeException cause = new RuntimeException(msg);
			log.error(msg, cause);
			response = new RpcTransportInvokeResponse(request, cause);	
		}

		if (log.isTraceEnabled()) {
			log.trace("Returning response [" + response + "]");
		}

		return response;
	}

	/** 
	 * Finds the methods in the service that are implemented by the serviceInterface, 
	 * and adds them to the serviceMethods Map. 
	 * 
	 * @param serviceInterface The interface being proxied
	 * @param service The service that implements the passed interface. 
	 * @return
	 */
	private ConcurrentMap<String, Method>  mineServiceMethods(Class<I> serviceInterface, I service) {
		ConcurrentMap<String, Method> serviceMethods = new ConcurrentHashMap<String, Method>();
		
		@SuppressWarnings("unchecked")
		Class<? extends I> serviceClass = (Class<? extends I>) service.getClass();
		Method[] serviceClassMethods = serviceClass.getMethods();
		
		for (Method serviceClassMethod : serviceClassMethods) {
			if (serviceClassMethod.getDeclaringClass() == serviceClass) {
				
				Method[] interfaceMethods = serviceInterface.getMethods();
				for (Method interfaceMethod : interfaceMethods) {
					if (interfaceMethod.getName().equals(serviceClassMethod.getName()) && 
						Arrays.equals(interfaceMethod.getParameterTypes(), serviceClassMethod.getParameterTypes())) {
						String interfaceMethodSignature = interfaceMethod.toGenericString();
						serviceMethods.put(interfaceMethodSignature,  serviceClassMethod);
					}					
				}
			}
		}
		
		if (serviceMethods.size() == 0) {
			log.warn("No mined methods for [" + serviceIdentifier + "] ");	
		}
		else if (log.isDebugEnabled()) {
			log.debug("Mined Methods for [" + serviceIdentifier + "], [" + serviceMethods + "]");	
		}
		
		return serviceMethods;
	}
	
	private Method getMethodForSignature(String methodSignatature) {
		Method method = serviceMethods.get(methodSignatature);
		return method;
	}

	@Override
	public String toString() {
		return "ServiceHandler [serviceIdentifier=" + serviceIdentifier
				+ ", serviceInterface=" + serviceInterface + ", service="
				+ service + "]";
	}
}
