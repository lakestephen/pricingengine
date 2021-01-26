package com.concurrentperformance.pebble.comms.rpc.connection.impl.out;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.caucho.hessian.io.Hessian2Output;
import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.comms.rpc.connection.impl.transport.RpcTransport;

/**
 * Asynchronous output stream that will accept any RpcTransport type and 
 * asynchronously write it to the output stream. 
 * 
 * @author Stephen Lake 
 */
public class AsynchRpcTransportOutputStream {

	private final Log log = LogFactory.getLog(this.getClass());

	private final RpcConnection connection;
	private Hessian2Output out;
	
	private final BlockingQueue<RpcTransport> outputQueue = new LinkedBlockingQueue<RpcTransport>(); //TODO could use disruptor
	
	private Thread writeQueuedTransportsToStreamThread;
	
	
	public AsynchRpcTransportOutputStream(RpcConnection connection) {
		this.connection = connection; 
	}
	
	public void start(Socket socket, String threadBaseName) throws IOException {
		log.debug("AsynchRpcTransportOutputStream starting for [" + connection + "]"); 
		this.out = new Hessian2Output(socket.getOutputStream());

		// start thread
		String enhancedThreadName = enhanceThreadName(threadBaseName);
		writeQueuedTransportsToStreamThread = new Thread(new WriteQueuedTransportsToStreamTask(), enhancedThreadName);
		writeQueuedTransportsToStreamThread.start();
	}
	
	public void writeRpcTransport(RpcTransport rpcTransport) {
		outputQueue.add(rpcTransport);
	}
	
	public void stop() {
		log.debug("AsynchRpcTransportOutputStream stopping for [" + connection + "]");  
		// we only interrupt if we are not the thread that initiated the shutdown.
		if (Thread.currentThread() != writeQueuedTransportsToStreamThread) { // TODO put this mech in all other connection threading bits. 
			writeQueuedTransportsToStreamThread.interrupt();
		}
	}
	
	
	private class WriteQueuedTransportsToStreamTask implements Runnable {
		@Override
		public void run() {
			if (out != null) {
				listenToOutputQueue(out);
			}
			
			log.debug("AsynchRpcTransportOutputStream thread exiting for [" + connection + "]"); 
		}
	
		private void listenToOutputQueue(Hessian2Output out) {
			while (!connection.isStoppedOrStopping()) { //TODO make more robust. 
				try {
					
					// block for the next request
					RpcTransport transport = outputQueue.take();
					if (log.isTraceEnabled()) {
						log.trace("Out [" +  transport + "] for [" + connection +"]");
					}
					// write to the stream.
					out.writeObject(transport);
					out.flush();
				} catch (InterruptedException e) {
					//TODO Must be able to handle spurious InterruptedException's
					final String msg = "InterruptedException while writing to input stream. Stopping.";
					connection.stopWithException(e, msg, log); 
				} catch (SocketException e) {
					final String msg = "SocketException while reading from input stream.  [" + e.getMessage() + "]. Stopping. (Other end may have closed) for [" + connection +"]";
					connection.stopWithException(e, msg, log);
				} catch (IOException e) { //TODO should we call stop on parent connection here ?
					final String msg = "IOException while reading from input stream.  [" + e.getMessage() + "]. Stopping for [" + connection +"]";
					connection.stopWithException(e, msg, log);
				}
			}
		}
	}


	public void setThreadName(String threadName) {
		writeQueuedTransportsToStreamThread.setName(enhanceThreadName(threadName));		
	}

	private static String enhanceThreadName(String threadName) {
		return threadName + ":w";
	}
}