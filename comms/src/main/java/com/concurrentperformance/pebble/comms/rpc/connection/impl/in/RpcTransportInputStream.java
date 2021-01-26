package com.concurrentperformance.pebble.comms.rpc.connection.impl.in;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.caucho.hessian.io.Hessian2Input;
import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.out.RemoteTransporter;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransport;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportControlRequest;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportInvokeRequest;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransportResponse;

/**
 * Task to read the input stream for RpcTransport objects, 
 * and pass to either the service call handler, or the response handler.
 * 
 * @author Stephen  Lake
 */
public class RpcTransportInputStream {

	private final Log log = LogFactory.getLog(this.getClass());
	
	private final RpcConnection connection;
	private Hessian2Input in;
	private final LocalServiceCallInvokeExecutor serviceCallInvokeExecutor;
	private final LocalServiceCallControlExecutor serviceCallControlExecutor;
	private RemoteTransporter blockingRemoteTransporter;
	private Thread listenAndActThread;
	
	public RpcTransportInputStream (RpcConnection connection, 
			LocalServiceCallInvokeExecutor serviceCallInvokeExecutor, 
			LocalServiceCallControlExecutor serviceCallControlExecutor, 
			RemoteTransporter blockingRemoteTransporter) {
		this.connection = connection;
		this.serviceCallInvokeExecutor = serviceCallInvokeExecutor;
		this.serviceCallControlExecutor = serviceCallControlExecutor; 
		this.blockingRemoteTransporter = blockingRemoteTransporter;
	}

	public void start(Socket socket, String threadBaseName) throws IOException {
		log.debug("RpcTransportInputStream started for [" + connection + "]");  
		this.in = new Hessian2Input(socket.getInputStream());

		// start thread
		String enhancedThreadName = enhanceThreadName(threadBaseName);
		listenAndActThread = new Thread(new ListenAndActTask(), enhancedThreadName);
		listenAndActThread.start();
	}
	
	public void stop() {
		log.debug("RpcTransportInputStream thread stopping for [" + connection + "]");  
		// we only interrupt if we are not the thread that initiated the shutdown.
		if (Thread.currentThread() != listenAndActThread) { // TODO put this mech in all other connection threading bits. 
			listenAndActThread.interrupt();
		}
	}

	/**
	 * Listens to the incoming object stream, and handles the transport object
	 * 
	 * @author Stephen Lake
	 */
	private class ListenAndActTask implements Runnable {

		@Override
		public void run() {
			
			if (in != null) {
				listenToInputStream(in);
			}
			
			log.debug("RpcTransportInputStream thread exiting for [" + connection + "]");  
	}
	
		private void listenToInputStream(Hessian2Input in) {
			while (!connection.isStoppedOrStopping()) { //TODO make more robust
				try {
					// read the response from the input stream 
					RpcTransport transport = (RpcTransport)in.readObject();

					if (log.isTraceEnabled()) {
						log.trace("In  [" +  transport + "]");
					}
					
					// handle depending on type.
					if (transport instanceof RpcTransportResponse) {
						handleAsServiceCallResponse((RpcTransportResponse)transport);
					}
					else if (transport instanceof RpcTransportInvokeRequest) {
						handleAsServiceCallInvokeRequest((RpcTransportInvokeRequest)transport);
					}
					
					else if (transport instanceof RpcTransportControlRequest) {
						handleAsServiceCallControlRequest((RpcTransportControlRequest)transport);
					}
					else {
							log.error("Unknown transport type [" + transport.getClass() + "] for transport [" + transport + "] for [" + connection +"]");
					}
					
					//TODO do we cope with spurious Interupts?
					
				} catch (SocketException e) {
					final String msg = "SocketException while reading from input stream.  [" + e.getMessage() + "]. Stopping. (Other end may have closed) for [" + connection +"]";
					connection.stopWithException(e, msg, log);
				} catch (IOException e) {
					final String msg = "IOException while reading from input stream.  [" + e.getMessage() + "] for [" + connection +"]";
					connection.stopWithException(e, msg, log);
				} 
			}
		}

	}
	
	private void handleAsServiceCallResponse(RpcTransportResponse response) {
		blockingRemoteTransporter.handleTransportResponse(response);
	}

	private void handleAsServiceCallInvokeRequest(final RpcTransportInvokeRequest invokeRequest) {
		serviceCallInvokeExecutor.handleTransportInvokeRequest(invokeRequest);
	}
	
	private void handleAsServiceCallControlRequest(RpcTransportControlRequest controlRequest) {
		serviceCallControlExecutor.handleTransportControlRequest(controlRequest);
	}

	public void setThreadName(String threadName) {
		final String enhancedThreadName = enhanceThreadName(threadName);
		listenAndActThread.setName(enhancedThreadName);		
	}
	
	private static String enhanceThreadName(String threadName) {
		return threadName + ":r";
	}
}