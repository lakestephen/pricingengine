package com.concurrentperformance.pebble.comms.rpc.connection.impl.out;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.common.connection.exception.ConnectionException;
import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportControlRequest;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportControlResponse;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportInvokeRequest;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportInvokeResponse;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportRequest;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportResponse;


/** 
 * Synchronous and asynchronous calling calling of requests.
 * 
 * Provides a mechanism that will accept transport requests, send them down the stream, 
 * and block the calling thread until there is a response. When a response is passed, 
 * the response thread finds the request, sets the response on the request, and 
 * unblocks the request thread so the request thread can return the response.  
 * 
 * @author Stephen Lake
 */
public class RemoteTransporter {

	private final Log log = LogFactory.getLog(this.getClass());

	static final int DEFAULT_CONCURRENCY = 32;
	
	private final RpcConnection connection;
	
	// The stream we are piping our requests to. 
	private final AsynchRpcTransportOutputStream outputStream;

	// map of all requests that we are waiting for a response. 
	private final ConcurrentMap<Long, RpcTransportRequest> outstandingRequestMap;

	
	/**
	 *  @param outputStream The stream that we pass the requests down.
	 *  @param concurrencyLevel The amount of threads that we expect will be making simultaneous 
	 *  calls on the remote interface.
	 */
	public RemoteTransporter(RpcConnection connection, AsynchRpcTransportOutputStream outputStream, int concurrencyLevel) {
		this.connection = connection ; 
		this.outputStream = outputStream;
		// The concurrency level is also used for the initial capacity because there will never 
		// be more items in the map, than threads accessing it due to the nature of the design.
		this.outstandingRequestMap = new ConcurrentHashMap<Long, RpcTransportRequest>(
				concurrencyLevel, 0.75f, concurrencyLevel); 
	}
	
	/**
	 * Construct a new RemoteBlockingRequestHandler using default concurrency.
	 * 
	 *  @param outputStream The stream that we pass the requests down.
	 *  calls on the remote interface.
	 */
	public RemoteTransporter(RpcConnection connection, AsynchRpcTransportOutputStream outputStream) {
		this(connection, outputStream, DEFAULT_CONCURRENCY);
	}

	public RpcTransportInvokeResponse transportRequestSynchronous(RpcTransportInvokeRequest request) {
		RpcTransportInvokeResponse  response = (RpcTransportInvokeResponse)transportSynchronousImpl(request); //TODO this cast is particularly ugly. Use the TransportType enum instead
		return response;
	}
	
	public RpcTransportControlResponse transportRequestSynchronous(RpcTransportControlRequest request) {
		RpcTransportControlResponse  response = (RpcTransportControlResponse)transportSynchronousImpl(request); //TODO this cast is particularly ugly. Use the TransportType enum instead
		return response;
	}
	
	/**
	 * Transport the request across the transport layer. This method will block until.
	 * a response is received. 
	 * //TODO need a timeout mech  
	 * @param request
	 * @return
	 */
	private RpcTransportResponse transportSynchronousImpl(RpcTransportRequest request) {
		if (connection.isStoppedOrStopping()) {
			throw new ConnectionException("Can't transport request because connection stopped. [" + connection +"]");
		}
		Long requestId = request.getRequestId();
		RpcTransportResponse response;		
		//Save the id and request to allow the incoming handler to match the response with the request. 
		outstandingRequestMap.put(requestId, request);
		outputStream.writeRpcTransport(request); 

		synchronized (request) {
		
			while ((response = request.getResponse()) == null) {
				try {
					request.wait(); //TODO make sure this does not wait forever if socket has cloased
				} catch (InterruptedException e) {
					// SJL handle properly
					log.error("", e);
				}
			} 
		}
		
		return response; 
	}

	/**
	 * Called when the response arrives back over the transport mech. Finds the original
	 * request, and frees the waiting thread to return the response. 
	 * 
	 * @param response
	 */
	public void handleTransportResponse(RpcTransportResponse response) {
		if (connection.isStoppedOrStopping()) {
			log.error("Ignoring response that arrived while connection stopped. response [" + response + "] for [" + connection +"]");
		}
		// find the request 
		RpcTransportRequest request = outstandingRequestMap.remove(response.getRequestId());

		if (request == null) {
			log.error("Cant find request object for response [" + response + "] for [" + connection +"]" );
		}
		else {
			// notify the waiting request thread that there is a response 
			notifyRequester(request, response);
		}
	}

	private void notifyRequester(RpcTransportRequest request, RpcTransportResponse response) {
		synchronized (request) {
			request.setResponse(response); 
			request.notifyAll(); 
		}
	}

	/**
	 * Stop and clear all outstanding requests.
	 */
	public void stop() {
		log.debug("BlockingRemoteTransporter stopping for [" + connection + "] for [" + connection +"]");  

		cancelOutstandingRequests();
	}

	/**
	 * Notify each outstanding request that they are not going to get a response by passing 
	 * in an exception.
	 */
	private void cancelOutstandingRequests() {
		while (!outstandingRequestMap.isEmpty()) {
			Set<Long> outstandingRequestIds = outstandingRequestMap.keySet();
			log.debug("Cancelling requests [" + outstandingRequestIds + "]");
			for (Long requestId : outstandingRequestIds) {
				// create a dummy response with an exception
				RpcTransportRequest request = outstandingRequestMap.remove(requestId);
				ConnectionException exception = new ConnectionException("Cancelled request [" + requestId + "] as connection stopped for [" + connection +"]");
				RpcTransportResponse response;

				if (request instanceof RpcTransportInvokeRequest) {
					response = new RpcTransportInvokeResponse((RpcTransportInvokeRequest)request, exception);
				}
				else if (request instanceof RpcTransportControlRequest) {
					response = new RpcTransportControlResponse((RpcTransportControlRequest)request, exception);
				}
				else {
					throw new IllegalStateException("Unhaldled transport type [" + request.getClass() + "] for [" + connection +"]");
				}
				
				notifyRequester(request, response);
			}
		}
	}
}
