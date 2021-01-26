package com.concurrentperformance.pebble.comms.common.connection.impl;

import java.io.IOException;
import java.net.Socket;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.common.connection.Connection;
import com.concurrentperformance.pebble.comms.common.connection.ConnectionListener;
import com.concurrentperformance.pebble.comms.common.connection.exception.ConnectionException;
import com.concurrentperformance.pebble.util.concurrent.ConcurrentHashSet;
import com.concurrentperformance.pebble.util.thread.ThreadNamer;

public abstract class SkelitalConnection implements Connection {
	
	private final Log log = LogFactory.getLog(this.getClass());

	private Socket socket;
	private String socketName;
	private String weAreA;
	private long weAreAId;
	private String weAreConnectingTo;
	private long weAreConnectingToId;
	private volatile boolean handshakeReceivedFromRemote = false; 

	private Set<ConnectionListener> listeners = new ConcurrentHashSet<ConnectionListener>();

	protected enum State {  
		STOPPED, 
		STARTING, 
		RUNNING, 
		IMMINENT_STOP_FROM_REMOTE,
		STOPPING, 
	} 

	private volatile State state = State.STOPPED; //TODO can this be private?
	
	@Override
	public void start() throws IOException {
		log.info("About to start connection [" + this + "] with [" + socket + "]. State[" + state + "]");

		if (this.socket == null) {
			throw new ConnectionException("No Socket set [" + this + "]");
		}
		if (!isStopped()) {
			throw new ConnectionException("Cant start as connection is not Stopped [" + this + "]");
		}

		state = State.STARTING;		
		
		startComponents(socket, socketName);
		
		performHandshake();

		waitForHandshakeFromRemote();
		
		updateThreadName();
 
		if (state != State.STARTING) { //TODO do this atomically
			log.error("State unexpectedly changed from [" + State.STARTING + "] to [" + state + "] during startup. Did an error occur during startup?  [" + this + "]");
		}
		else {
			state = State.RUNNING;	
			notifyStarted();
			log.info("Started connection [" + this + "] with [" + socket + "]. State[" + state + "]");
		}
	}
	
	protected abstract void startComponents(Socket socket, String threadBaseName) throws IOException;
	protected abstract void performHandshake();
	
	@Override
	public void handshakeRecieved(String weAreConnectingTo, long weAreConnectingToId) {
		this.weAreConnectingTo = weAreConnectingTo;
		this.weAreConnectingToId = weAreConnectingToId;
		this.handshakeReceivedFromRemote = true;
		log.info("Handshake recieved [" + this + "]");
	}
	
	private void waitForHandshakeFromRemote() {
		while (!handshakeReceivedFromRemote) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				//As in loop - swallow.
			}
		}
		log.trace("Handshake recieved");
	}
	
	@Override
	public void stop() {
		/* 
		 * We return if we are stopped, or stopping because of two related cases:
		 * 1) We have an abnormal stop, and that may cause both the input and the output 
		 * streams to throw exceptions, and we only want a single abnormalStop triggered
		 * else the second may interfere with the state transition of the first.
		 * 2) We have a normal stop, but killing the socket causes 1). 
		 */
		if (isStoppedOrStopping()) {
			log.info("Connection already stopped [" + this + "]");
			return;
		}
		log.info("Stopping connection [" + this + "]");

		signalImminentStopToRemoteSocket();
		doStop();
		notifyStopped(true);
	}
	
	protected abstract void signalImminentStopToRemoteSocket();
	
	@Override
	public void imminentStop() {
		log.info("Imminent Stop [" + this + "]");
		state = State.IMMINENT_STOP_FROM_REMOTE; 
	}
	
	public void stopWithException(Exception cause, String msg, Log otherLogger) {

		boolean expectedStop = false;
		/* 
		 * We do a normal stop if in IMMINENT_STOP state as this is 
		 * an expected stop that the other side told us about.
		 */
		if (state == State.IMMINENT_STOP_FROM_REMOTE) {
			otherLogger.debug(msg);
			expectedStop = true;
			log.info("Stopping connection [" + this + "]");
		}
		/* 
		 * We return if we are stopped, or stopping because of two related cases:
		 * 1) We have an abnormal stop, and that may cause both the input and the output 
		 * streams to throw exceptions, and we only want a single abnormalStop triggered
		 * else the second may interfere with the stat transition of thr first.
		 * 2) We have a normal stop, but killing the socket causes 1). 
		 */
		else if (isStoppedOrStopping()) {
			otherLogger.debug(msg);
			return;
		}
		else {
			otherLogger.error(msg, cause);
			log.error("Abnormal stop for connection  [" + this + "]" );
		}
		
		doStop();		
		notifyStopped(expectedStop);
	}

	private void doStop() {
		state = State.STOPPING;
		stopSocket();
		
		stopComponents();
		
		state = State.STOPPED;
		log.info("Connection stopped  [" + this + "]" );
		weAreConnectingTo = null;
	}
	
	protected abstract void stopComponents();
	
	private void stopSocket() {
		log.debug("Socket stopping [" + this + "]" );
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				log.error("Problem closing socket socket [" + socket + "] in stop. [" + this + "]", e);
			}
		}
		socket = null;
		log.debug("Socket stopped [" + this + "]" );
	}

	@Override
	public boolean isStoppedOrStopping() { 
		boolean stoppedOrStopping = (state == State.STOPPED) ||
						  (state == State.STOPPING);
		return stoppedOrStopping;
	}
	
	@Override
	public boolean isStopped() {
		boolean stopped = (state == State.STOPPED);
		return stopped;
	}
	
	protected void notifyStarted() {
		log.debug("Notifying listeners that connection has started [" + listeners + "] ");
		for (ConnectionListener listener : listeners) {
			listener.connection_notifyStarted(this);
		}
	}

	protected void notifyStopped(boolean expected) {
		log.debug("Notifying listeners that connection has stopped [" + 
					(expected? "normally" : "unexpectedly") + "] listeners[" + listeners + "] ");
		for (ConnectionListener listener : listeners) {
			listener.connection_notifyStopped(this, expected);
		}
	}

	@Override
	public void register(ConnectionListener listener) {
		listeners.add(listener);
	}

	@Override
	public void deregister(ConnectionListener listener) {
		listeners.remove(listener);		
	}
	
	@Override
	public final void setSocket(Socket socket) {
		this.socket = socket;
		this.socketName = ThreadNamer.getThreadName(socket);
	}
	
	private void updateThreadName() {
		String threadBaseName = "";
		if (socketName != null) {
			threadBaseName += socketName;
		}
		if (socketName != null && weAreConnectingTo != null) {
			threadBaseName += ":";
		}
		if (weAreConnectingTo != null) {
			threadBaseName += weAreConnectingTo;
		}
		
		doUpdateThreadName(threadBaseName);
	}
	
	protected abstract void doUpdateThreadName(String threadBaseName);


	public final String getWeAreA() {
		return weAreA;
	}

	public final void setWeAreA(String weAreA) {
		this.weAreA = weAreA;
	}

	@Override
	public final long getWeAreAId() {
		return weAreAId;
	}

	public final void setWeAreAId(long weAreAId) {
		this.weAreAId = weAreAId;
	}

	public final void setWeAreConnectingTo(String weAreConnectingTo) {
		this.weAreConnectingTo = weAreConnectingTo;
	}
	
	@Override
	public final String getWeAreConnectingTo() {
		return weAreConnectingTo;
	}

	public final void setWeAreConnectingToId(long weAreConnectingToId) {
		this.weAreConnectingToId = weAreConnectingToId;
	}
	
	@Override
	public final long getWeAreConnectingToId() {
		return weAreConnectingToId;
	}
	
	@Override 
	public String toString() { 
		return "" + this.getClass().getSimpleName() + " [" + weAreA + "(" + weAreAId + ")>" + weAreConnectingTo + "(" + weAreConnectingToId + "), " + socketName + "," + state + "]";
	}
}
