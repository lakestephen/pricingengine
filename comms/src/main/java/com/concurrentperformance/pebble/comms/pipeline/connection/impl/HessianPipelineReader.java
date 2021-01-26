package com.concurrentperformance.pebble.comms.pipeline.connection.impl;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.caucho.hessian.io.Hessian2Input;
import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineConnection;
import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineReader;
import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineReceiver;
import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineTranslator;

public class HessianPipelineReader<T> implements PipelineReader {

	private final Log log = LogFactory.getLog(this.getClass());

	private final PipelineConnection connection;
	
	private final CommandTranslator commandTranslator;
	private PipelineTranslator<T> translator;
	private PipelineReceiver<T> receiver;

	private Hessian2Input in;
	private Thread listenAndActThread;
	
	HessianPipelineReader(PipelineConnection connection) {
		this.connection = connection;
		commandTranslator = new CommandTranslator(connection);
	}

	void start(Socket socket, String threadBaseName) throws IOException {
		this.in = new Hessian2Input(socket.getInputStream());

		String enhancedThreadName = enhanceThreadName(threadBaseName);
		listenAndActThread = new Thread(new ListenAndActTask(), enhancedThreadName);
		listenAndActThread.start();
	}
	
	private static String enhanceThreadName(String threadName) {
		return threadName + ":r";
	}	

	@Override
	public String readString() throws IOException {
		return in.readString();
	}
	
	@Override
	public byte readByte() throws IOException {
		return in.readBytes()[0]; //TODO why cant we read / write a single byte or char?
	}
	
	@Override
	public int readInt() throws IOException {
		return in.readInt();
	}

	@Override
	public long readLong() throws IOException {
		return in.readLong();
	}

	
	public void stop() {
		log.debug("HessianPipelineReader stopping for [" + connection + "]");  
		if (Thread.currentThread() != listenAndActThread) { // TODO put this mech in all other connection threading bits. 
			listenAndActThread.interrupt();
		}
	}
	
	public final void setReceiver(PipelineReceiver<T> receiver) {
		this.receiver = receiver;
	}
	
	public final void setTranslator(PipelineTranslator<T> translator) {
		this.translator = translator;
	}
	
	/**
	 * Listens to the incoming object stream, and handles the receiver
	 * 
	 * @author Stephen Lake
	 */
	private class ListenAndActTask implements Runnable {

		@Override
		public void run() {
			while (!connection.isStoppedOrStopping()) { //TODO make more robust. 
				try {
					// we read the discriminator 
					byte discriminator = readByte();

					// give the standard translator a chance to handle.
					boolean handled = handleItem(discriminator);
					
					// if not, handle as a command
					if (!handled) {
						handled = handleCommand(discriminator);
					}
					
					// Otherwise ...
					if (!handled) {
						throw new IllegalStateException("Unhandled pipeline command [" + discriminator + "]");
					}
				} catch (SocketException e) {
					final String msg = "SocketException while reading from input stream.  [" + e.getMessage() + "]. Stopping. (Other end may have closed) for [" + connection +"]";
					connection.stopWithException(e, msg, log);
				} catch (IOException e) { //TODO should we call stop on parent connection here ?
					final String msg = "IOException while reading from input stream.  [" + e.getMessage() + "]. Stopping for [" + connection +"]";
					connection.stopWithException(e, msg, log);
				}
			}
		}

		private boolean handleItem(byte discriminator) throws IOException {
			boolean handled = false;
			T item = null;
			item = translator.readNext(discriminator, HessianPipelineReader.this);
			if (item != null) {
				if (log.isTraceEnabled()) { 
					log.trace("Read from wire [" + item + "]" + System.nanoTime()/1000 );
				}
				receiver.receive(item);
				handled = true;
			}
			return handled;
		}

		private boolean handleCommand(byte discriminator) throws IOException {
			Boolean handled = commandTranslator.readNext(discriminator, HessianPipelineReader.this);
			return handled;
		}
	}

	public void setThreadName(String threadBaseName) {
		String enhancedThreadName = enhanceThreadName(threadBaseName);
		listenAndActThread.setName(enhancedThreadName);
	}

}