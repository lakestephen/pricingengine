package com.concurrentperformance.pebble.comms.rpc.connection.impl.in;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.common.connection.exception.ConnectionException;
import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.comms.rpc.connection.IncommingServiceDefinition;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.out.AsynchRpcTransportOutputStream;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportInvokeRequest;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportInvokeResponse;
import com.concurrentperformance.pebble.util.thread.ThreadFactoryBuilder;

public class LocalServiceCallInvokeExecutor {

	private final Log log = LogFactory.getLog(this.getClass());

	private final RpcConnection connection;
	private final AsynchRpcTransportOutputStream outputStream;

	private ExecutorService serviceCallExecutor;
	private Map <String, ServiceCallHandler<? extends Object>> serviceCallHandlers = 
			new ConcurrentHashMap<String, ServiceCallHandler<? extends Object>>(); 
	private Map<Class<?>, Object> services = new ConcurrentHashMap<Class<?>, Object>(); 
	private final ServiceCallHandler<? extends Object> nullServiceHandler = new NullServiceCallHandler();
	
	public LocalServiceCallInvokeExecutor(RpcConnection connection, AsynchRpcTransportOutputStream outputStream) {
		this.connection = connection;
		this.outputStream = outputStream;
	}
	
	public void start(String threadBaseName) {
		String enhancedThreadName = enhanceThreadName(threadBaseName);
		this.serviceCallExecutor = createAndStartExecutor(enhancedThreadName);
	}

	private ExecutorService createAndStartExecutor(String threadName) {
		log.debug("LocalServiceCallInvokeExecutor starting executor for [" + connection + "]"); //TODO name the connection
		//TODO make these values configurable
		int corePoolSize = 0; //Never less that this number of threads 
		int maximumPoolSize = 10; //Never more than this number of threads 
		long keepAliveTimeMs = TimeUnit.MILLISECONDS.convert(60L, TimeUnit.SECONDS); //Timeout before a thread is allowed to die.
		ThreadFactory threadFactory = ThreadFactoryBuilder.BuildThreadFactory(threadName);
		
		ExecutorService executor =  new ThreadPoolExecutor(
										corePoolSize, 
        								maximumPoolSize,
        								keepAliveTimeMs, 
        								TimeUnit.MILLISECONDS,
        								new SynchronousQueue<Runnable>(),
        								threadFactory);
		return executor;        
	}

	public void handleTransportInvokeRequest(final RpcTransportInvokeRequest invokeRequest) {
		// create and execute a task for executing the service call		
		Runnable requestTask = new Runnable() {
			
			@Override
			public void run() {
				if (log.isTraceEnabled()) {
					log.trace("Invoke [" + invokeRequest + "]");
				}
				
				// find the service handler and invoke
				ServiceCallHandler<? extends Object> serviceHandler = getServiceHandler(invokeRequest);

				// handle invoking the method
				try {
					RpcTransportInvokeResponse response = serviceHandler.invoke(invokeRequest);
					outputStream.writeRpcTransport(response);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		serviceCallExecutor.execute(requestTask); //If changing to submit() think about how an exception is propagated / logged.
	}

	private ServiceCallHandler<? extends Object> getServiceHandler(RpcTransportInvokeRequest request) {
		String serviceIdentifier = request.getServiceIdentifier();
		ServiceCallHandler<? extends Object> serviceHandler = serviceCallHandlers.get(serviceIdentifier);
		
		if (serviceHandler == null) {
			log.warn("Can't find handler for serviceIdentifier [" + serviceIdentifier+ "], available [" + serviceCallHandlers.keySet() + "]");
			serviceHandler = nullServiceHandler;
		}
		
		return serviceHandler;
	}
	
	public final void setIncommingServicDefinitions(Set<IncommingServiceDefinition<? extends Object, ? extends Object>> incommingServiceDefinitions) {
	
		for (IncommingServiceDefinition<? extends Object, ? extends Object> incommingServiceDefinition : incommingServiceDefinitions) {
			String serviceIdentifier = incommingServiceDefinition.getIncommingServiceIdentifier();
			Class<? extends Object> serviceInterface = incommingServiceDefinition.getIncommingServiceInterface();
			Object incommingService = incommingServiceDefinition.getIncommingService();
			
			if (serviceInterface == null) {
				throw new ConnectionException("Service interface must not be null for service [" + serviceIdentifier + "]");
			}
			if (!serviceInterface.isInterface()) {
				throw new ConnectionException("Service interface [" + serviceInterface + "] must be an interface.  For service [" + serviceIdentifier + "]");
			}
			if (incommingService == null) {
				throw new ConnectionException("Service must not be null.");
			}
			if (!serviceInterface.isAssignableFrom(incommingService.getClass())) {
				throw new ConnectionException("Service [" + incommingService.getClass() + "] is not assignable from ServiceInterface [" + serviceInterface + "] . For service [" + serviceIdentifier + "]");
			}
	
			ServiceCallHandler<? extends Object> serviceHandler =  //TODO sort typing
					new DefaultServiceCallHandler(incommingService, serviceInterface, serviceIdentifier);
			serviceCallHandlers.put(serviceIdentifier, serviceHandler);
			
			services.put(serviceInterface, incommingService);
		}
	}

	public void stop() {
		log.debug("LocalServiceCallInvokeExecutor stopping executor for [" + connection + "]"); //TODO name the connection 

		serviceCallExecutor.shutdown();
		try {
			serviceCallExecutor.awaitTermination(10, TimeUnit.SECONDS);
			log.debug("LocalServiceCallInvokeExecutor threads shutdown for [" + connection + "]");  
		} catch (InterruptedException e) {
			log.error("LocalServiceCallInvokeExecutor threads failed to shutdown for [" + connection + "]");  
		}

	}

	public void setThreadName(String threadName) {
		//TODO
	}
	
	private static String enhanceThreadName(String threadName) {
		return threadName + ":c";
	}
}
